package com.learning.hotelManagementSystem.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AppUtil {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final SecureRandom secureRandom=new SecureRandom();

    public String generateOTPKey(String userName) {
        return "email-verification - "+userName;
    }

    public String generateOTP() {
        final int OTP=secureRandom.nextInt(900000) + 100000;
        return Integer.toString(OTP);
    }

    public String generateOTPHash(String OTP) {
        return passwordEncoder.encode(OTP);
    }

    public boolean matchOTP(String enteredOTP, String storedHash) {
        return passwordEncoder.matches(enteredOTP, storedHash);
    }
}
