package com.learning.hotelManagementSystem.DTO.BookingDTO;

import com.learning.hotelManagementSystem.types.BookingStatus;

public record CancelBookingResponse(long bookingId, BookingStatus bookingStatus) {
}
