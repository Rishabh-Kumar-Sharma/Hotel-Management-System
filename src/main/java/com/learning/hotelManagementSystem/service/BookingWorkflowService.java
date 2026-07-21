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

@Service
@Slf4j
public class BookingWorkflowService {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;

    public CreateBookingResponse createBooking(CreateBookingRequest request) {
        CreateBookingReponseInternal bookingResponse=bookingService.createBooking(request.roomId(), request.checkIn(), request.checkOut());
        CreateOrderRequest req=new CreateOrderRequest(bookingResponse.amount(),bookingResponse.currency(),request.receiptId(),bookingResponse.bookingId());
        CreateOrderResponse orderResponse=paymentService.createOrder(req);

        bookingService.updateBookingOrderId(bookingResponse.bookingId(),orderResponse.orderId());
        bookingService.updateBookingReceiptId(bookingResponse.bookingId(),request.receiptId());

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
    }

    public ConfirmBookingResponse confirmBooking(ConfirmBookingRequest request) {
        ConfirmBookingResponse res=bookingService.confirmBooking(request.bookingId());
        boolean isPaymentSuccess=paymentService.verifyPayment(request);

        if(!isPaymentSuccess) {
            bookingService.updateBookingStatus(request.bookingId(), BookingStatus.FAILED);
            throw new PaymentException(Translations.BOOKING_CANT_BE_CONFIRMED);
        }

        bookingService.updateBookingPaymentId(request.bookingId(),request.razorpay_payment_id());
        return new ConfirmBookingResponse(res.bookingId(),res.bookingStatus());
    }

    public CancelBookingResponse cancelBooking(long bookingId) {
        Booking booking=bookingService.getBookingDetails(bookingId);
        if(booking.getBookingStatus()==BookingStatus.CONFIRMED) {
            paymentService.refundPayment(new RefundPaymentRequest(bookingId,booking.getRoom().getPricePerNight()));
        }

        return bookingService.cancelBooking(bookingId);
    }
}
