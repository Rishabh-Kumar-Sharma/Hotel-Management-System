package com.learning.hotelManagementSystem.translations;

public interface Translations {
//    Errors:-

    public final String BOOKING_IN_PROGRESS="Room is currently being booked, please try again";
    public final String NO_ROOMS_AVAILABLE="No Bookings available for selected room in the specified time";
    public final String INVALID_CHECK_IN_CHECK_OUT_TIMES="Invalid check-in and check-out times";
    public final String CUSTOMER_DOES_NOT_EXIST="Customer doesn't exist";
    public final String ROOM_NOT_FOUND="No Room Found!";
    public final String ROOM_ALREADY_EXISTS="Room with this room number already exists";
    public final String BOOKING_DOES_NOT_EXIST="Booking not found!";
    public final String BOOKING_CANT_BE_CONFIRMED="Booking can't be confirmed in this state";
    public final String BOOKING_ALREADY_EXPIRED="Booking has already expired!";
    public final String BOOKING_ALREADY_COMPLETED="Already completed bookings can't be cancelled";
    public final String CUSTOMER_DELETED_SUCCESSFULLY="Customer deleted successfully";
    public final String PAST_CHECK_IN_DATE="Bookings can't be made for past dates";
    public final String USER_ALREADY_EXISTS="User already exists";
    public final String USER_DOES_NOT_EXIST="User doesn't exist";
    public final String TOKEN_EXPIRED="Authentication expired! Please login again.";
    public final String INVALID_CREDENTIALS="Invalid Credentials";
    public final String UNAUTHORIZED_ACCESS ="Please login first";
    public final String PAYMENT_FAILED="Payment Failed";
    public final String PAYMENT_VERIFICATION_FAILED="Payment Verification Failed. Please try again.";
    public final String PAYMENT_REFUND_FAILED="Payment Refund Failed";
    public final String PAYMENT_DOES_NOT_EXIST="Payment doesn't exist";
    public final String REFUND_AMOUNT_GREATER_THAN_PAID="Refund amount can't be more than the amount paid";
    public final String BOOKING_ALREADY_STARTED="Booking has already started";
}
