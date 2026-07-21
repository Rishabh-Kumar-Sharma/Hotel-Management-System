package com.learning.hotelManagementSystem.controllers;

import com.learning.hotelManagementSystem.DTO.BookingDTO.*;
import com.learning.hotelManagementSystem.DTO.BookingDTO.ConfirmBookingRequest;
import com.learning.hotelManagementSystem.service.BookingService;
import com.learning.hotelManagementSystem.service.BookingWorkflowService;
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

    @Autowired
    private BookingWorkflowService bookingWorkflowService;

    @PostMapping("/createBooking")
    public ResponseEntity<CreateBookingResponse> createBooking(@RequestBody CreateBookingRequest bookingRequest) {
        CreateBookingResponse bookingResponse=bookingWorkflowService.createBooking(bookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponse);
    }

    @PostMapping("/confirmBooking")
    public ResponseEntity<ConfirmBookingResponse> confirmBooking(@RequestBody ConfirmBookingRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingWorkflowService.confirmBooking(request));
    }

    @GetMapping("/cancelBooking/{bookingId}")
    public ResponseEntity<CancelBookingResponse> cancelBooking(@PathVariable long bookingId) {
        return ResponseEntity.status(HttpStatus.OK).
                body(bookingWorkflowService.cancelBooking(bookingId));
    }

    @GetMapping("/getAllBookings")
    public ResponseEntity<Map<String, List<GetBookingResponse>>> fetchAllBookings() {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("bookings",bookingService.getAllBookings()));
    }
}


