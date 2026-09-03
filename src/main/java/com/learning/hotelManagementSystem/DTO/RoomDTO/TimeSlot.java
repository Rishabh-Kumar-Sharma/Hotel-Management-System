package com.learning.hotelManagementSystem.DTO.RoomDTO;

import java.time.Instant;

public record TimeSlot(Instant checkIn, Instant checkOut) {
}
