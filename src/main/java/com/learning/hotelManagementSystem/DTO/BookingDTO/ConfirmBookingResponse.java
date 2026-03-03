package com.learning.hotelManagementSystem.DTO.BookingDTO;

import com.learning.hotelManagementSystem.types.BookingStatus;

public record ConfirmBookingResponse(long bookingId, BookingStatus bookingStatus) {
}
