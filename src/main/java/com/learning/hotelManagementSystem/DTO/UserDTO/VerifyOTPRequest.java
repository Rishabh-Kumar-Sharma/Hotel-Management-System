package com.learning.hotelManagementSystem.DTO.UserDTO;

public record VerifyOTPRequest(long userId, String userName, String OTP) {
}
