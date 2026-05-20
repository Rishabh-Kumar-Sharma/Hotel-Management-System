package com.learning.hotelManagementSystem.DTO.BookingDTO;

import com.learning.hotelManagementSystem.types.RoomType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GetBookingResponse(LocalDateTime checkIn, LocalDateTime checkOut, BigDecimal pricePerNight, int roomNumber, RoomType roomType) {
}
