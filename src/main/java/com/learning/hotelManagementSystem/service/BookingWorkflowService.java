package com.learning.hotelManagementSystem.service;

import com.learning.hotelManagementSystem.DTO.BookingDTO.*;
import com.learning.hotelManagementSystem.DTO.PaymentDTO.CreateOrderRequest;
import com.learning.hotelManagementSystem.DTO.PaymentDTO.CreateOrderResponse;
import com.learning.hotelManagementSystem.DTO.PaymentDTO.RefundPaymentRequest;
import com.learning.hotelManagementSystem.entity.Booking;
import com.learning.hotelManagementSystem.exceptions.PaymentException;
import com.learning.hotelManagementSystem.translations.Translations;
import com.learning.hotelManagementSystem.types.BookingStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class BookingWorkflowService {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;

    public CreateBookingResponse createBooking(CreateBookingRequest request) {
        CreateBookingReponseInternal bookingResponse = bookingService.createBooking(request.roomId(), request.checkIn(), request.checkOut());
        try {
            CreateOrderRequest req = new CreateOrderRequest(bookingResponse.amount(), bookingResponse.currency(), request.receiptId(), bookingResponse.bookingId());
            CreateOrderResponse orderResponse = paymentService.createOrder(req);

            bookingService.updateOrderDetails(req.bookingId(), orderResponse.receiptId(), orderResponse.orderId());

            return new CreateBookingResponse(
                    bookingResponse.bookingId(),
                    bookingResponse.bookingStatus(),
                    bookingResponse.checkIn(),
                    bookingResponse.checkOut(),
                    bookingResponse.amount(),
                    bookingResponse.currency(),
                    orderResponse.orderId(),
                    orderResponse.receiptId()
            );
        } catch(Exception e) {
            bookingService.updateBookingStatus(bookingResponse.bookingId(),BookingStatus.FAILED);
            throw e;
        }
    }

    public ConfirmBookingResponse confirmBooking(ConfirmBookingRequest request) {
        boolean isPaymentSuccess=false;
        try {
            isPaymentSuccess = paymentService.verifyPayment(request);

            if (!isPaymentSuccess) {
                bookingService.updateBookingStatus(request.bookingId(), BookingStatus.FAILED);
                throw new PaymentException(Translations.BOOKING_CANT_BE_CONFIRMED);
            }

            ConfirmBookingResponse res = bookingService.confirmBooking(request.bookingId(), request.razorpay_payment_id());
            return new ConfirmBookingResponse(res.bookingId(), res.bookingStatus());
        } catch(Exception e) {
            try {
                final Booking booking = bookingService.getBookingDetails(request.bookingId());
                if (isPaymentSuccess) {
                    paymentService.refundPayment(new RefundPaymentRequest(booking.getId(), booking.getRoom().getPricePerNight()));
                }
            } catch(Exception refundException) {
                log.error("Refund Exception = {}",request.bookingId(),refundException);
            } finally {
                bookingService.updateBookingStatus(request.bookingId(),BookingStatus.FAILED);
            }

            throw e;
        }
    }

    public CancelBookingResponse cancelBooking(long bookingId) {
        Booking booking = bookingService.getBookingDetails(bookingId);
        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            paymentService.refundPayment(new RefundPaymentRequest(bookingId, booking.getRoom().getPricePerNight()));
        }

        return bookingService.cancelBooking(bookingId);
    }

    public GetBookingResponse updateBooking(UpdateBookingDetailsRequest request) {
        try {
            Booking booking=bookingService.updateBookingDetails(request.bookingId(),request.checkIn(),request.checkOut());
            return new GetBookingResponse(
                    booking.getCheckIn(),
                    booking.getCheckOut(),
                    booking.getRoom().getPricePerNight(),
                    booking.getRoom().getRoomNumber(),
                    booking.getRoom().getType(),
                    booking.getBookingStatus(),
                    booking.getId(),
                    booking.getExpiresAt(),
                    booking.getOrderId(),
                    booking.getPaymentId(),
                    "INR"
            );
        } catch(Exception e) {
            throw e;
        }
    }
}
