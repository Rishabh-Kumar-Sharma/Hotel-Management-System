package com.learning.hotelManagementSystem.DTO.RoomDTO;

import com.learning.hotelManagementSystem.entity.Room;

import java.util.List;

public record GetAllAvailableRoomsResponse(List<RoomResponse> rooms) {
}
