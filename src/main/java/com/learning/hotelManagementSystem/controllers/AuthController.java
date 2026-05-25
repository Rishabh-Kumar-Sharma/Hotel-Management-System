package com.learning.hotelManagementSystem.controllers;

import com.learning.hotelManagementSystem.DTO.UserDTO.CreateUserRequest;
import com.learning.hotelManagementSystem.DTO.UserDTO.CreateUserResponse;
import com.learning.hotelManagementSystem.DTO.UserDTO.LoginUserRequest;
import com.learning.hotelManagementSystem.DTO.UserDTO.LoginUserResponse;
import com.learning.hotelManagementSystem.security.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/test")
    public String Test() {
        return "Working";
    }

    @PostMapping("/signup")
    public ResponseEntity<CreateUserResponse> signup(@RequestBody CreateUserRequest customerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(customerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(@RequestBody LoginUserRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequest));
    }
}
