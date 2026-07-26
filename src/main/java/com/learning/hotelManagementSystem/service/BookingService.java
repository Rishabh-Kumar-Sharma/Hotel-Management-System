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

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
    public CreateBookingReponseInternal createBooking(long roomId, Instant checkIn, Instant checkOut) {
        try {
            final String username= SecurityContextHolder.getContext().getAuthentication().getName();
            User user=userRepository.findUserByUserName(username).orElseThrow(()->new EntityNotFoundException(Translations.USER_DOES_NOT_EXIST));
            Customer customer=customerRepository.findCustomerByUser(user).orElseThrow(()->new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST));
            if(checkIn.isBefore(Instant.now())) {
                throw new IllegalArgumentException(Translations.PAST_CHECK_IN_DATE);
            }

            if (checkIn.isAfter(checkOut) || checkIn.equals(checkOut)) {
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

            return new CreateBookingReponseInternal(booking.getId(),booking.getBookingStatus(),booking.getCheckIn(),booking.getCheckOut(), room.getPricePerNight(), "INR");
        } catch (PessimisticLockException | LockTimeoutException e) {
            throw new EntityNotAvailableException(Translations.BOOKING_IN_PROGRESS);
        }
    }

    @Transactional
    public ConfirmBookingResponse confirmBooking(Long bookingId, String paymentId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->new EntityNotFoundException(Translations.BOOKING_DOES_NOT_EXIST));
        if(booking.getBookingStatus()==BookingStatus.CONFIRMED) {
            return new ConfirmBookingResponse(bookingId,booking.getBookingStatus());
        }
        if(booking.getBookingStatus()==BookingStatus.EXPIRED || booking.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException(Translations.BOOKING_ALREADY_EXPIRED);
        }
        if(booking.getBookingStatus()!=BookingStatus.CREATED) {
            throw new IllegalStateException(Translations.BOOKING_CANT_BE_CONFIRMED);
        }

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setPaymentId(paymentId);
        booking.setExpiresAt(booking.getCheckOut());

        return new ConfirmBookingResponse(bookingId,booking.getBookingStatus());
    }

    public List<GetBookingResponse> getAllBookings() {
        final String username=SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userRepository.findUserByUserName(username).orElseThrow(()->new EntityNotFoundException(Translations.USER_DOES_NOT_EXIST));
        Customer customer=customerRepository.findCustomerByUser(user).orElseThrow(()->new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST));
        long customerId=customer.getId();
        List<Booking> bookings=bookingRepository.getAllBookingsOfCustomer(customerId,bookingStatuses,Instant.now());
        List<GetBookingResponse> response=bookings.stream().map(booking->
                new GetBookingResponse(
                        booking.getCheckIn(),
                        booking.getCheckOut(),
                        booking.getRoom().getPricePerNight(),
                        booking.getRoom().getRoomNumber(),
                        booking.getRoom().getType(),
                        booking.getBookingStatus(),
                        booking.getId(),
                        booking.getExpiresAt(),
                        booking.getOrderId(),
                        booking.getPaymentId()
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
        if(bookingStatus==BookingStatus.CREATED && booking.getCheckOut().isBefore(Instant.now())) {
            booking.setBookingStatus(BookingStatus.EXPIRED);
            return new CancelBookingResponse(bookingId,booking.getBookingStatus());
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);

        return new CancelBookingResponse(bookingId,booking.getBookingStatus());
    }

    public Booking getBookingDetails(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(()->new EntityNotFoundException(Translations.BOOKING_DOES_NOT_EXIST));
    }

    @Transactional
    public void updateBookingStatus(long bookingId, BookingStatus bookingStatus) {
        final Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->new EntityNotFoundException(Translations.BOOKING_DOES_NOT_EXIST));
        booking.setBookingStatus(bookingStatus);
    }

    @Transactional
    public void updateOrderDetails(long bookingId, String receiptId, String orderId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->new EntityNotFoundException(Translations.BOOKING_DOES_NOT_EXIST));
        booking.setOrderId(orderId);
        if(receiptId!=null) {
            booking.setReceiptId(receiptId);
        }
    }
}
