package com.learning.hotelManagementSystem.DTO.PaymentDTO;

import java.math.BigDecimal;

public record RefundPaymentRequest(long bookingId, BigDecimal amount) {
}
