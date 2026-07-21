package com.learning.hotelManagementSystem.DTO.PaymentDTO;

public record CreateOrderResponse( String orderId, int amount, String currency, String receiptId) {
}
