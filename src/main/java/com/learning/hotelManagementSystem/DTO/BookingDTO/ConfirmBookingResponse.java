package com.learning.hotelManagementSystem.DTO.BookingDTO;

public record ConfirmBookingResponse(long bookingId,
                                     com.learning.hotelManagementSystem.types.BookingStatus bookingStatus) {
}
