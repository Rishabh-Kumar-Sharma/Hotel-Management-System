package com.learning.hotelManagementSystem.DTO.BookingDTO;

import java.time.LocalDateTime;

public record CreateBookingRequest(long roomId, LocalDateTime checkIn, LocalDateTime checkOut) {
}
