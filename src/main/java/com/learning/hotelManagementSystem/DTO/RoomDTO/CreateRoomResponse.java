package com.learning.hotelManagementSystem.DTO.RoomDTO;

import com.learning.hotelManagementSystem.types.RoomType;

import java.math.BigDecimal;

public record CreateRoomResponse(long RoomId, BigDecimal pricePerNight, int roomNumber, int capacity, RoomType roomType) {
}
