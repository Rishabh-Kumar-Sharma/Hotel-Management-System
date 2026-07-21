package com.learning.hotelManagementSystem.DTO.BookingDTO;

import java.time.Instant;

public record CreateBookingRequest(long roomId, Instant checkIn, Instant checkOut, String receiptId) {
}
