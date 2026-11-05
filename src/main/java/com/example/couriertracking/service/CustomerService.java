package com.example.couriertracking.service;

import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.couriertracking.entity.Customer;
import com.example.couriertracking.repository.CustomerRepository;

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer saveCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException(
                    "A customer with this email already exists.");
        }
        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found with ID: " + id));
    }

    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        Customer existingCustomer = getCustomerById(id);
        existingCustomer.setName(updatedCustomer.getName());
        existingCustomer.setEmail(updatedCustomer.getEmail());
        existingCustomer.setPhone(updatedCustomer.getPhone());
        existingCustomer.setAddress(updatedCustomer.getAddress());

        return customerRepository.save(existingCustomer);
    }
    
    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
    }
    
    @Transactional
    public Customer updateCustomerProfile(
            Long customerId,
            String name,
            String email,
            String phone,
            String address) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer not found."));

        name = name.trim();
        email = email.trim().toLowerCase(java.util.Locale.ROOT);
        phone = phone.trim();
        address = address.trim();

        if (name.isBlank()
                || email.isBlank()
                || phone.isBlank()
                || address.isBlank()) {

            throw new IllegalArgumentException(
                    "All profile fields are required.");
        }

        if (customerRepository.existsByEmailAndIdNot(
                email, customerId)) {

            throw new IllegalArgumentException(
                    "This email address is already used by another customer.");
        }

        if (customerRepository.existsByPhoneAndIdNot(
                phone, customerId)) {

            throw new IllegalArgumentException(
                    "This phone number is already used by another customer.");
        }

        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(address);

        return customerRepository.save(customer);
    }
}