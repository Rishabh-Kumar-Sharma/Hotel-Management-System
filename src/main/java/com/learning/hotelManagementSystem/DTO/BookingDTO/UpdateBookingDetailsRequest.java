package com.learning.hotelManagementSystem.DTO.BookingDTO;

import java.time.Instant;

public record UpdateBookingDetailsRequest(long bookingId, Instant checkIn, Instant checkOut) {
}
