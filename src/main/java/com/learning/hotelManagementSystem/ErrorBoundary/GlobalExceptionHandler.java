package com.learning.hotelManagementSystem.ErrorBoundary;

import com.learning.hotelManagementSystem.exceptions.*;
import com.razorpay.RazorpayException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(e.getMessage(),ApiErrorCodesEnum.NOT_FOUND.name()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(e.getMessage(),ApiErrorCodesEnum.BAD_REQUEST.name()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(e.getMessage(),ApiErrorCodesEnum.BAD_REQUEST.name()));
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ApiError> handleDuplicateEntity(DuplicateEntityException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(e.getMessage(),ApiErrorCodesEnum.DUPLICATE_ENTITY.name()));
    }

    @ExceptionHandler(EntityNotAvailableException.class)
    public ResponseEntity<ApiError> handleEntityNotAvailable(EntityNotAvailableException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(e.getMessage(),ApiErrorCodesEnum.ENTITY_NOT_AVAILABLE.name()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(e.getMessage(),ApiErrorCodesEnum.UNAUTHORIZED_ACCESS.name()));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiError> handleRazorpayException(RazorpayException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(e.getMessage(),ApiErrorCodesEnum.RAZORPAY_ERROR.name()));
    }
}
