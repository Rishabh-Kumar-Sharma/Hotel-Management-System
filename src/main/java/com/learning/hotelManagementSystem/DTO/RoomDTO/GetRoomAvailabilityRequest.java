package com.learning.hotelManagementSystem.DTO.RoomDTO;

import java.time.Instant;

public record GetRoomAvailabilityRequest(long bookingId, long roomNumber, Instant checkIn, Instant checkOut) {
}
