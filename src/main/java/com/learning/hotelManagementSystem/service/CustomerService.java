package com.learning.hotelManagementSystem.service;

import com.learning.hotelManagementSystem.DTO.CustomerDTO.CreateCustomerRequest;
import com.learning.hotelManagementSystem.DTO.CustomerDTO.CreateCustomerResponse;
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
    public CreateCustomerResponse createCustomer(CreateCustomerRequest customer) {
        Optional<Customer> customer1=customerRepository.findByEmailId(customer.emailId());
        if(customer1.isPresent()) {
            if(customer1.get().isActive()) {
                throw new DuplicateEntityException(Translations.CUSTOMER_EMAIL_ID_ALREADY_EXISTS);
            } else {
                Customer currCustomer=customer1.get();
                currCustomer.setActive(true);
                currCustomer.setName(customer.name());
                currCustomer.setEmailId(customer.emailId());
                currCustomer.setContactNo(customer.contactNo());
                return new CreateCustomerResponse(currCustomer.getId(),currCustomer.getName(),currCustomer.getEmailId(),currCustomer.getContactNo());
            }
        } else {
            customer1=customerRepository.findByContactNo(customer.contactNo());
            if (customer1.isPresent()) {
                if(customer1.get().isActive()) {
                    throw new DuplicateEntityException(Translations.CUSTOMER_CONTACT_ALREADY_EXISTS);
                }
                else {
                    Customer currCustomer=customer1.get();
                    currCustomer.setActive(true);
                    return new CreateCustomerResponse(currCustomer.getId(),currCustomer.getName(),currCustomer.getEmailId(),currCustomer.getContactNo());
                }
            }
        }

        Customer customer2=new Customer(customer.name(),customer.emailId(),customer.contactNo());

        customerRepository.save(customer2);
        return new CreateCustomerResponse(customer2.getId(),customer2.getName(),customer2.getEmailId(),customer2.getContactNo());
    }

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

    public Customer getCustomerDetailsByEmailId(String emailId) {
        return customerRepository.findByEmailId(emailId).orElseThrow(()->new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST));
    }

    public Customer getCustomerDetailsByContactNo(String contactNo) {
        return customerRepository.findByContactNo(contactNo).orElseThrow(()->new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST));
    }

    @Transactional
    public void updateCustomerDetails(long id, Customer newCustomerDetails) {
        Customer customer=customerRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(Translations.CUSTOMER_DOES_NOT_EXIST));
        customer.setName(newCustomerDetails.getName());
        customer.setContactNo(newCustomerDetails.getContactNo());
        customer.setEmailId(newCustomerDetails.getEmailId());
    }
}
