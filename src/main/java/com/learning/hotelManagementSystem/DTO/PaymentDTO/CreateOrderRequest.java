package com.learning.hotelManagementSystem.DTO.PaymentDTO;

import java.math.BigDecimal;

public record CreateOrderRequest(BigDecimal amount, String currency, String receiptId, long bookingId) {
}
