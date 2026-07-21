package com.learning.hotelManagementSystem.DTO.BookingDTO;

public record ConfirmBookingRequest(String razorpay_order_id, String razorpay_payment_id, String razorpay_signature, long bookingId) {
}
