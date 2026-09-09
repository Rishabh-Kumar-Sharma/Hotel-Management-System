package com.learning.hotelManagementSystem.controllers;

import com.learning.hotelManagementSystem.DTO.UserDTO.*;
import com.learning.hotelManagementSystem.security.AuthService;
import com.learning.hotelManagementSystem.service.EmailVerificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/signup")
    public ResponseEntity<CreateUserResponse> signup(@RequestBody CreateUserRequest customerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(customerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(@RequestBody LoginUserRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequest));
    }

    @GetMapping("/fetchCurrentUser")
    public ResponseEntity<LoginUserResponse> getCurrentUser(@RequestHeader("Authorization") String authToken) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.getUserData(authToken));
    }

    @PostMapping("/verifyOTP")
    public ResponseEntity<VerifyOTPResponse> verifyOTP(@RequestBody VerifyOTPRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(emailVerificationService.verifyOTP(request));
    }

    @PostMapping("/resendOTP")
    public ResponseEntity<VerifyEmailResponse> resendOTP(@RequestBody VerifyEmailRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(emailVerificationService.sendAndSaveOTP(request));
    }
}
