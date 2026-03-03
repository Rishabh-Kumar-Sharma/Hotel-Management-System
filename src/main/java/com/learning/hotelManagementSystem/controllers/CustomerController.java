package com.learning.hotelManagementSystem.controllers;

import com.learning.hotelManagementSystem.DTO.CustomerDTO.CreateCustomerRequest;
import com.learning.hotelManagementSystem.DTO.CustomerDTO.CreateCustomerResponse;
import com.learning.hotelManagementSystem.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/createCustomer")
    public ResponseEntity<CreateCustomerResponse> createCustomer(@RequestBody CreateCustomerRequest customerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(customerRequest));
    }

    @DeleteMapping("/deleteCustomer/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.deleteCustomer(id));
    }

}
