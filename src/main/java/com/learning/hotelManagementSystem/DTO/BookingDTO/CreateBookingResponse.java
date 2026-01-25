package com.learning.hotelManagementSystem.DTO.BookingDTO;

import com.learning.hotelManagementSystem.types.BookingStatus;

import java.time.LocalDateTime;

public record CreateBookingResponse(long bookingId, BookingStatus bookingStatus, LocalDateTime checkIn, LocalDateTime checkOut) {
}
