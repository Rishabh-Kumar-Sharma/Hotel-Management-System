package com.learning.hotelManagementSystem.service;

import com.learning.hotelManagementSystem.entity.Customer;
import com.learning.hotelManagementSystem.entity.User;
import com.learning.hotelManagementSystem.repository.CustomerRepository;
import com.learning.hotelManagementSystem.repository.UserRepository;
import com.learning.hotelManagementSystem.translations.Translations;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Transactional
    public String deleteCustomer() {
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userRepository.findUserByUserName(username).orElseThrow(()->new EntityNotFoundException(Translations.USER_DOES_NOT_EXIST));
        Customer customer=customerRepository.findCustomerByUser(user).orElseThrow(()-> new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST));

        if(!customer.isActive()) throw new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST);

        customer.setActive(false);
        return Translations.CUSTOMER_DELETED_SUCCESSFULLY;
    }

    public Customer getCustomerDetailsById(long id) {
        Customer customer=customerRepository.findById(id).orElseThrow(()->new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST));
        if(!customer.isActive()) throw new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST);
        return customer;
    }

//    public Customer getCustomerDetailsByEmailId(String emailId) {
//        return customerRepository.findByEmailId(emailId).orElseThrow(()->new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST));
//    }
//
//    public Customer getCustomerDetailsByContactNo(String contactNo) {
//        return customerRepository.findByContactNo(contactNo).orElseThrow(()->new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST));
//    }

    @Transactional
    public void updateCustomerDetails(long id, Customer newCustomerDetails) {
        Customer customer=customerRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST));
//        customer.setName(newCustomerDetails.getName());
//        customer.setContactNo(newCustomerDetails.getContactNo());
//        customer.setEmailId(newCustomerDetails.getEmailId());
    }
}
