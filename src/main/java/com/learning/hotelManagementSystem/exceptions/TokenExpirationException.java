package com.learning.hotelManagementSystem.exceptions;

public class TokenExpirationException extends RuntimeException {
    public TokenExpirationException(String message) {
        super(message);
    }
}
