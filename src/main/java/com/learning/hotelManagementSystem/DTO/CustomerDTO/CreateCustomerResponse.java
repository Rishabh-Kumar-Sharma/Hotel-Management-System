package com.learning.hotelManagementSystem.DTO.CustomerDTO;

public record CreateCustomerResponse(long CustomerId, String name, String email, String contactNo) {
}
