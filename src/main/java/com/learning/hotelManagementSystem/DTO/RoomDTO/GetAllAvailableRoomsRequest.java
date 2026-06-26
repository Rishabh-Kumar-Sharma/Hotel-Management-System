package com.learning.hotelManagementSystem.DTO.RoomDTO;

import java.time.Instant;

public record GetAllAvailableRoomsRequest(Instant checkIn, Instant checkOut) {
}
