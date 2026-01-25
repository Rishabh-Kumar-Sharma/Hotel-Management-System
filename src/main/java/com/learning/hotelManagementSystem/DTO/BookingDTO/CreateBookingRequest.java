package com.learning.hotelManagementSystem.DTO.BookingDTO;

import java.time.LocalDateTime;

public record CreateBookingRequest(long customerId, long roomId, LocalDateTime checkIn, LocalDateTime checkOut) {
}
