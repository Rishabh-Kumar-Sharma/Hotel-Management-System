package com.learning.hotelManagementSystem.controllers;

import com.learning.hotelManagementSystem.DTO.RoomDTO.CreateRoomRequest;
import com.learning.hotelManagementSystem.DTO.RoomDTO.CreateRoomResponse;
import com.learning.hotelManagementSystem.DTO.RoomDTO.GetAllAvailableRoomsRequest;
import com.learning.hotelManagementSystem.DTO.RoomDTO.GetAllAvailableRoomsResponse;
import com.learning.hotelManagementSystem.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    RoomService roomService;

    @PostMapping("/createRoom")
    public ResponseEntity<CreateRoomResponse> createRoom(@RequestBody CreateRoomRequest roomRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.addRoom(roomRequest));
    }

    @PostMapping("/availableRooms")
    public ResponseEntity<GetAllAvailableRoomsResponse> getAllAvailableRoomsResponse(@RequestBody GetAllAvailableRoomsRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.getAvailableRooms(request.checkIn(),request.checkOut()));
    }
}
