package com.learning.hotelManagementSystem.DTO.BookingDTO;

import com.learning.hotelManagementSystem.types.BookingStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateBookingResponse(long bookingId, BookingStatus bookingStatus, Instant checkIn, Instant checkOut, BigDecimal amount, String currency, String orderId, String receiptId) {
}
