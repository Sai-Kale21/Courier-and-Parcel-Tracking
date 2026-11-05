package com.example.couriertracking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.couriertracking.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);
    long count();

    boolean existsByPhone(String phone);
    
    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneAndIdNot(String phone, Long id);
}