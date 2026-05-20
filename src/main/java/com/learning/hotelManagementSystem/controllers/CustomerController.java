package com.learning.hotelManagementSystem.controllers;

import com.learning.hotelManagementSystem.DTO.CustomerDTO.CreateCustomerRequest;
import com.learning.hotelManagementSystem.DTO.CustomerDTO.CreateCustomerResponse;
import com.learning.hotelManagementSystem.DTO.CustomerDTO.GetCustomerRequest;
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

    @PostMapping("/createCustomer")
    public void createCustomer(@RequestBody CreateCustomerRequest customerRequest) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(customerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<CreateCustomerResponse> getCustomerDetails(@RequestBody GetCustomerRequest customerRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getCustomerDetails(customerRequest));
    }

    @DeleteMapping("/deleteCustomer/{id}")
    public ResponseEntity<Map<String,String>> deleteCustomer(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message",customerService.deleteCustomer(id)));
    }
}
