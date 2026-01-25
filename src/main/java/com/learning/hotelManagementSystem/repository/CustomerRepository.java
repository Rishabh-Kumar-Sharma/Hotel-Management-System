package com.learning.hotelManagementSystem.repository;

import com.learning.hotelManagementSystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    public Optional<Customer> findByEmailId(String emailId);
    public boolean existsByEmailId(String emailId);
    public boolean existsByContactNo(String contactNo);
    public Optional<Customer> findByContactNo(String contactNo);
}
