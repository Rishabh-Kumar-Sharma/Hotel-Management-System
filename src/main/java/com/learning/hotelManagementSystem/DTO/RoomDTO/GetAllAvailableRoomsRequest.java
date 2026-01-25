package com.learning.hotelManagementSystem.DTO.RoomDTO;

import java.time.LocalDateTime;

public record GetAllAvailableRoomsRequest(LocalDateTime checkIn, LocalDateTime checkOut) {
}
