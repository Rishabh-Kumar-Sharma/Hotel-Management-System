package com.learning.hotelManagementSystem.DTO.RoomDTO;

import com.learning.hotelManagementSystem.types.RoomStatus;
import com.learning.hotelManagementSystem.types.RoomType;

import java.math.BigDecimal;

public record UpdateRoomRequest(long id, BigDecimal pricePerNight, Integer roomNumber, Integer capacity, RoomType roomType, RoomStatus roomStatus) {
}
