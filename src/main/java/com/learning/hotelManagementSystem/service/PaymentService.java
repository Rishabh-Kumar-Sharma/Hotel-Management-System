package com.learning.hotelManagementSystem.service;

import com.learning.hotelManagementSystem.DTO.BookingDTO.ConfirmBookingRequest;
import com.learning.hotelManagementSystem.DTO.PaymentDTO.*;
import com.learning.hotelManagementSystem.entity.Booking;
import com.learning.hotelManagementSystem.exceptions.PaymentException;
import com.learning.hotelManagementSystem.translations.Translations;
import com.learning.hotelManagementSystem.types.RefundStatus;
import com.razorpay.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    @Value("${razorpay.api.secret}")
    private String RAZORPAY_API_SECRET;

   @Autowired
   private RazorpayClient razorpayClient;

    @Autowired
    private BookingService bookingService;

    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        try {
            JSONObject orderRequest = new JSONObject();
            BigDecimal amount = request.amount();
            String currency = request.currency();
            String receiptId = request.receiptId();
            orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100L)).longValue()); // RazorPay requires amount in Paise
            orderRequest.put("currency", currency);

            if(receiptId!=null) orderRequest.put("receipt", receiptId);

            final Order order = razorpayClient.orders.create(orderRequest);
            final String orderId=order.get("id");

            String receipt = order.toJson().optString("receipt");
            if (receipt.isEmpty()) {
                receipt = null;
            }

            return new CreateOrderResponse(orderId, order.get("amount"), order.get("currency"), receipt);
        } catch(RazorpayException e) {
            throw new PaymentException(Translations.PAYMENT_FAILED);
        } catch(EntityNotFoundException e) {
            throw new EntityNotFoundException(e);
        }
    }

    public boolean verifyPayment(ConfirmBookingRequest request) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.razorpay_order_id());
            options.put("razorpay_payment_id", request.razorpay_payment_id());
            options.put("razorpay_signature", request.razorpay_signature());

            return Utils.verifyPaymentSignature(options, RAZORPAY_API_SECRET);
        } catch (RazorpayException e) {
            throw new PaymentException(Translations.PAYMENT_VERIFICATION_FAILED);
        }
    }

    public RefundPaymentResponse refundPayment(RefundPaymentRequest request) {
        try {
            long bookingId= request.bookingId();
            Booking booking=bookingService.getBookingDetails(bookingId);

            String paymentId=booking.getPaymentId();

            if (paymentId == null) {
                throw new PaymentException(Translations.PAYMENT_DOES_NOT_EXIST);
            }

            Payment payment=razorpayClient.payments.fetch(paymentId);
            JSONObject refundRequest = new JSONObject();

            long amount=((Number) payment.get("amount")).longValue();
            long refundedAmount=((Number)payment.get("amount_refunded")).longValue();

            long maxRefundable=amount-refundedAmount;

            if(request.amount()!=null) {

                final long amountRequested = request.amount().multiply(BigDecimal.valueOf(100L)).longValue();

                if (amountRequested > maxRefundable)
                    throw new PaymentException(Translations.REFUND_AMOUNT_GREATER_THAN_PAID);

                refundRequest.put("amount", amountRequested);
            }

            refundRequest.put("speed", "normal");
            if(booking.getReceiptId()!=null) {
                refundRequest.put("receipt", booking.getReceiptId());
            }
            Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);
            String status = refund.get("status");

            RefundStatus refundStatus;

            switch (status) {
                case "processed":
                    refundStatus = RefundStatus.PROCESSED;
                    break;
                case "pending":
                    refundStatus = RefundStatus.PENDING;
                    break;
                default:
                    refundStatus = RefundStatus.FAILED;
                    break;
            }

            return new RefundPaymentResponse(refundStatus);

        } catch (RazorpayException e) {
            log.error("Refund failed", e);
            throw new PaymentException(Translations.PAYMENT_REFUND_FAILED);
        } catch(EntityNotFoundException e) {
            throw new EntityNotFoundException(e);
        }
    }
}
