package com.learning.hotelManagementSystem.controllers;

import com.learning.hotelManagementSystem.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @DeleteMapping("/deleteCustomer")
    public ResponseEntity<Map<String,String>> deleteCustomer() {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message",customerService.deleteCustomer()));
    }
}
