package com.learning.hotelManagementSystem.service;

import com.learning.hotelManagementSystem.DTO.BookingDTO.*;
import com.learning.hotelManagementSystem.entity.Booking;
import com.learning.hotelManagementSystem.entity.Customer;
import com.learning.hotelManagementSystem.entity.Room;
import com.learning.hotelManagementSystem.entity.User;
import com.learning.hotelManagementSystem.exceptions.EntityNotAvailableException;
import com.learning.hotelManagementSystem.repository.BookingRepository;
import com.learning.hotelManagementSystem.repository.CustomerRepository;
import com.learning.hotelManagementSystem.repository.RoomRepository;
import com.learning.hotelManagementSystem.repository.UserRepository;
import com.learning.hotelManagementSystem.translations.Translations;
import com.learning.hotelManagementSystem.types.BookingStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    private static final List<BookingStatus> bookingStatuses =
            List.of(BookingStatus.CREATED, BookingStatus.CONFIRMED);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public CreateBookingResponse createBooking(long roomId, LocalDateTime checkIn, LocalDateTime checkOut) {
        try {
            final String username= SecurityContextHolder.getContext().getAuthentication().getName();
            User user=userRepository.findUserByUserName(username).orElseThrow(()->new EntityNotFoundException(Translations.USER_DOES_NOT_EXIST));
            Customer customer=customerRepository.findCustomerByUser(user).orElseThrow(()->new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST));
            if(checkIn.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException(Translations.PAST_CHECK_IN_DATE);
            }

            if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
                throw new IllegalArgumentException(Translations.INVALID_CHECK_IN_CHECK_OUT_TIMES);
            }

            if (!customer.isActive()) throw new IllegalArgumentException(Translations.CUSTOMER_DOES_NOT_EXIST);

            Room room = roomRepository.findByIdForUpdate(roomId);
            if (room == null) throw new EntityNotFoundException(Translations.ROOM_NOT_FOUND);

            List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(roomId, checkIn, checkOut, bookingStatuses);
            if (!overlappingBookings.isEmpty()) {
                throw new IllegalStateException(Translations.NO_ROOMS_AVAILABLE);
            }

            Booking booking = new Booking(checkIn, checkOut, BookingStatus.CREATED, customer, room);
            bookingRepository.save(booking);

            return new CreateBookingResponse(booking.getId(),booking.getBookingStatus(),booking.getCheckIn(),booking.getCheckOut());
        } catch (PessimisticLockException | LockTimeoutException e) {
            throw new EntityNotAvailableException(Translations.BOOKING_IN_PROGRESS);
        }
    }

    @Transactional
    public ConfirmBookingResponse confirmBooking(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->new EntityNotFoundException(Translations.BOOKING_DOES_NOT_EXIST));
        if(booking.getBookingStatus()==BookingStatus.CONFIRMED) {
            return new ConfirmBookingResponse(bookingId,booking.getBookingStatus());
        }
        if(booking.getBookingStatus()==BookingStatus.EXPIRED || booking.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(Translations.BOOKING_ALREADY_EXPIRED);
        }
        if(booking.getBookingStatus()!=BookingStatus.CREATED) {
            throw new IllegalStateException(Translations.BOOKING_CANT_BE_CONFIRMED);
        }

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setExpiresAt(booking.getCheckOut());

        return new ConfirmBookingResponse(bookingId,booking.getBookingStatus());
    }

    public List<GetBookingResponse> getAllBookings() {
        final String username=SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userRepository.findUserByUserName(username).orElseThrow(()->new EntityNotFoundException(Translations.USER_DOES_NOT_EXIST));
        Customer customer=customerRepository.findCustomerByUser(user).orElseThrow(()->new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST));
        long customerId=customer.getId();
        List<Booking> bookings=bookingRepository.getAllBookingsOfCustomer(customerId,bookingStatuses,LocalDateTime.now());
        List<GetBookingResponse> response=bookings.stream().map(booking->
                new GetBookingResponse(
                        booking.getCheckIn(),
                        booking.getCheckOut(),
                        booking.getRoom().getPricePerNight(),
                        booking.getRoom().getRoomNumber(),
                        booking.getRoom().getType(),
                        booking.getBookingStatus(),
                        booking.getId(),
                        booking.getExpiresAt()
                )
        ).toList();

        return response;
    }

    @Transactional
    public CancelBookingResponse cancelBooking(long bookingId) {
        Booking booking=bookingRepository.findByIdForUpdate(bookingId);

        if(booking==null) throw new EntityNotFoundException(Translations.BOOKING_DOES_NOT_EXIST);

        BookingStatus bookingStatus=booking.getBookingStatus();
        if(bookingStatus==BookingStatus.CANCELLED || bookingStatus==BookingStatus.EXPIRED) {
            return new CancelBookingResponse(bookingId,bookingStatus);
        }
        if(bookingStatus==BookingStatus.COMPLETED) {
            throw new IllegalStateException(Translations.BOOKING_ALREADY_COMPLETED);
        }
        if(bookingStatus==BookingStatus.CREATED && booking.getCheckOut().isBefore(LocalDateTime.now())) {
            booking.setBookingStatus(BookingStatus.EXPIRED);
            return new CancelBookingResponse(bookingId,booking.getBookingStatus());
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);

        return new CancelBookingResponse(bookingId,booking.getBookingStatus());
    }
}
