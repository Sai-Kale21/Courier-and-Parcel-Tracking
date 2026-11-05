package com.example.couriertracking.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.couriertracking.entity.AppUser;
import com.example.couriertracking.entity.Customer;
import com.example.couriertracking.repository.AppUserRepository;
import com.example.couriertracking.repository.CustomerRepository;

@Service
public class CustomerRegistrationService {

private final CustomerRepository customerRepository;
private final AppUserRepository appUserRepository;
private final PasswordEncoder passwordEncoder;

public CustomerRegistrationService(
        CustomerRepository customerRepository,
        AppUserRepository appUserRepository,
        PasswordEncoder passwordEncoder) {

    this.customerRepository = customerRepository;
    this.appUserRepository = appUserRepository;
    this.passwordEncoder = passwordEncoder;
}

@Transactional
public void registerCustomer(
        String name,
        String email,
        String phone,
        String address,
        String username,
        String password) {

    name = name.trim();
    email = email.trim().toLowerCase(Locale.ROOT);
    phone = phone.trim();
    address = address.trim();
    username = username.trim();

    if (customerRepository.existsByEmail(email)) {
        throw new IllegalArgumentException(
                "This email address is already registered.");
    }

    if (customerRepository.existsByPhone(phone)) {
        throw new IllegalArgumentException(
                "This phone number is already registered.");
    }

    if (appUserRepository.existsByUsername(username)) {
        throw new IllegalArgumentException(
                "This username is already taken.");
    }

    Customer customer = new Customer();

    customer.setName(name);
    customer.setEmail(email);
    customer.setPhone(phone);
    customer.setAddress(address);

    Customer savedCustomer =
            customerRepository.save(customer);

    AppUser appUser = new AppUser();

    appUser.setUsername(username);
    appUser.setPassword(passwordEncoder.encode(password));
    appUser.setRole("CUSTOMER");
    appUser.setEnabled(true);
    appUser.setCustomer(savedCustomer);

    appUserRepository.save(appUser);
}

}
