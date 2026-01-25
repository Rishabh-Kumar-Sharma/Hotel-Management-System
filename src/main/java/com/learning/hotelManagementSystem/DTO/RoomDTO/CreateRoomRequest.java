package com.learning.hotelManagementSystem.DTO.RoomDTO;

import com.learning.hotelManagementSystem.types.RoomType;

import java.math.BigDecimal;

public record CreateRoomRequest(BigDecimal pricePerNight, int roomNumber, int capacity, RoomType roomType) {
}
