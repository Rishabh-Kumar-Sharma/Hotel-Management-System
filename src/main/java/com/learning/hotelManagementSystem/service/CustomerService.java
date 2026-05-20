package com.learning.hotelManagementSystem.service;

import com.learning.hotelManagementSystem.DTO.CustomerDTO.CreateCustomerRequest;
import com.learning.hotelManagementSystem.DTO.CustomerDTO.CreateCustomerResponse;
import com.learning.hotelManagementSystem.DTO.CustomerDTO.GetCustomerRequest;
import com.learning.hotelManagementSystem.entity.Customer;
import com.learning.hotelManagementSystem.exceptions.DuplicateEntityException;
import com.learning.hotelManagementSystem.repository.CustomerRepository;
import com.learning.hotelManagementSystem.translations.Translations;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public String deleteCustomer(long id) {
        Customer customer=customerRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST));

        if(!customer.isActive()) throw new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST);

        customer.setActive(false);
        return Translations.CUSTOMER_DELETED_SUCCESSFULLY;
    }

    public Customer getCustomerDetailsById(long id) {
        Customer customer=customerRepository.findById(id).orElseThrow(()->new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST));
        if(!customer.isActive()) throw new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST);
        return customer;
    }

    public CreateCustomerResponse getCustomerDetails(GetCustomerRequest customerRequest) {
        Customer customer=customerRepository.findById(customerRequest.id()).orElseThrow(()->new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST));
        if(!customer.isActive()) throw new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST);
        return new CreateCustomerResponse(customer.getId(),"","","132");
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
