package com.learning.hotelManagementSystem;

import com.learning.hotelManagementSystem.entity.Customer;
import com.learning.hotelManagementSystem.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class CustomerTests {

    @Autowired
    private CustomerService customerService;

    @Test
    public void createCustomer() {
        Customer customer=new Customer("Rishabh","test@test.com","1234567890");
        customerService.createCustomer(customer);
    }

    @Test
    public void deleteCustomer() {
        customerService.deleteCustomer(1L);
    }
}
