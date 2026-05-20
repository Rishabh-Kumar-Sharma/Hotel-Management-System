package com.learning.hotelManagementSystem.controllers;

import com.learning.hotelManagementSystem.DTO.BookingDTO.*;
import com.learning.hotelManagementSystem.entity.Booking;
import com.learning.hotelManagementSystem.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/createBooking")
    public ResponseEntity<CreateBookingResponse> createBooking(@RequestBody CreateBookingRequest bookingRequest) {
        CreateBookingResponse bookingResponse=bookingService.createBooking(bookingRequest.customerId(),bookingRequest.roomId(),bookingRequest.checkIn(),bookingRequest.checkOut());
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponse);
    }

    @PostMapping("/confirmBooking/{bookingId}")
    public ResponseEntity<ConfirmBookingResponse> confirmBooking(@PathVariable long bookingId) {
        return ResponseEntity.status(HttpStatus.OK).
                body(bookingService.confirmBooking(bookingId));
    }

    @GetMapping("/cancelBooking/{bookingId}")
    public ResponseEntity<CancelBookingResponse> cancelBooking(@PathVariable long bookingId) {
        return ResponseEntity.status(HttpStatus.OK).
                body(bookingService.cancelBooking(bookingId));
    }

    @PostMapping("/getAllBookings")
    public ResponseEntity<Map<String, List<GetBookingResponse>>> fetchAllBookings(@RequestBody GetAllBookingsRequest bookingsRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("booking",bookingService.getAllBookings(bookingsRequest)));
    }
}


