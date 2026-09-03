package com.learning.hotelManagementSystem.DTO.RoomDTO;

import com.learning.hotelManagementSystem.types.RoomAvailabilityEnum;

import java.util.List;

public record GetRoomAvailabilityResponse(long roomNumber, RoomAvailabilityEnum status, List<TimeSlot> availableSlots) {
}
