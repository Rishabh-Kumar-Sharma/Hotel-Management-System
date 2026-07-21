package com.learning.hotelManagementSystem.controllers;

import com.learning.hotelManagementSystem.DTO.PaymentDTO.*;
import com.learning.hotelManagementSystem.service.PaymentService;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    PaymentService paymentService;

    @PostMapping("/refund-payment")
    public ResponseEntity<RefundPaymentResponse> refundPayment(@RequestBody RefundPaymentRequest request) {
        return ResponseEntity.status(200).body(paymentService.refundPayment(request));
    }
}
