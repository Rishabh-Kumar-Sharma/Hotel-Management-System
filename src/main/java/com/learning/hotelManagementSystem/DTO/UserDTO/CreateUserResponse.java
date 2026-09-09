package com.learning.hotelManagementSystem.DTO.UserDTO;

public record CreateUserResponse(String userName, long id, boolean isOTPSent, String name) {
}
