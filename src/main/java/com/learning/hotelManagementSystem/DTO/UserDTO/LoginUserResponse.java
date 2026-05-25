package com.learning.hotelManagementSystem.DTO.UserDTO;

public record LoginUserResponse(String authToken, long id, String userName, String contactNo) {
}
