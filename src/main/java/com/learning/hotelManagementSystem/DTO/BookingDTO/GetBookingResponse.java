package com.learning.hotelManagementSystem.DTO.BookingDTO;

import com.learning.hotelManagementSystem.types.BookingStatus;
import com.learning.hotelManagementSystem.types.RoomType;

import java.math.BigDecimal;
import java.time.Instant;

public record GetBookingResponse(Instant checkIn, Instant checkOut, BigDecimal pricePerNight, int roomNumber, RoomType roomType, BookingStatus bookingStatus, Long bookingId, Instant expiresAt, String orderId, String paymentId) {
}
