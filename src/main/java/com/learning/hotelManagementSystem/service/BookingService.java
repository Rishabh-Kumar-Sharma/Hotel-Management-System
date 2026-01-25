package com.learning.hotelManagementSystem.service;

import com.learning.hotelManagementSystem.DTO.BookingDTO.CancelBookingResponse;
import com.learning.hotelManagementSystem.DTO.BookingDTO.ConfirmBookingResponse;
import com.learning.hotelManagementSystem.entity.Booking;
import com.learning.hotelManagementSystem.entity.Customer;
import com.learning.hotelManagementSystem.entity.Room;
import com.learning.hotelManagementSystem.exceptions.EntityNotAvailableException;
import com.learning.hotelManagementSystem.repository.BookingRepository;
import com.learning.hotelManagementSystem.repository.RoomRepository;
import com.learning.hotelManagementSystem.DTO.BookingDTO.CreateBookingResponse;
import com.learning.hotelManagementSystem.translations.Translations;
import com.learning.hotelManagementSystem.types.BookingStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RoomRepository roomRepository;

    @Transactional
    public CreateBookingResponse createBooking(long customerId, long roomId, LocalDateTime checkIn, LocalDateTime checkOut) {
        try {
            if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
                throw new IllegalArgumentException(Translations.INVALID_CHECK_IN_CHECK_OUT_TIMES);
            }

            Customer customer = customerService.getCustomerDetailsById(customerId);

            if (!customer.isActive()) throw new IllegalArgumentException(Translations.CUSTOMER_DOES_NOT_EXIST);

            Room room = roomRepository.findByIdForUpdate(roomId);
            if (room == null) throw new EntityNotFoundException(Translations.ROOM_NOT_FOUND);

            List<BookingStatus> bookingStatuses = List.of(BookingStatus.CREATED, BookingStatus.CONFIRMED);

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

        return new ConfirmBookingResponse(bookingId,booking.getBookingStatus());
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
