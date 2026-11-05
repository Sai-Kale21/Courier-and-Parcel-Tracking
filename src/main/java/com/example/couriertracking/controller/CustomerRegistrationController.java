package com.example.couriertracking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.couriertracking.service.CustomerRegistrationService;

@Controller
public class CustomerRegistrationController {

private final CustomerRegistrationService registrationService;

public CustomerRegistrationController(
        CustomerRegistrationService registrationService) {

    this.registrationService = registrationService;
}

@GetMapping("/register")
public String showRegistrationForm() {
    return "register";
}

@PostMapping("/register")
public String registerCustomer(
        @RequestParam String name,
        @RequestParam String email,
        @RequestParam String phone,
        @RequestParam String address,
        @RequestParam String username,
        @RequestParam String password,
        @RequestParam String confirmPassword,
        Model model,
        RedirectAttributes redirectAttributes) {

    if (name.isBlank()
            || email.isBlank()
            || phone.isBlank()
            || address.isBlank()
            || username.isBlank()
            || password.isBlank()) {

        model.addAttribute(
                "errorMessage",
                "All fields are required.");

        return "register";
    }

    if (!password.equals(confirmPassword)) {
        model.addAttribute(
                "errorMessage",
                "Passwords do not match.");

        return "register";
    }

    if (password.length() < 8) {
        model.addAttribute(
                "errorMessage",
                "Password must contain at least 8 characters.");

        return "register";
    }

    try {
        registrationService.registerCustomer(
                name,
                email,
                phone,
                address,
                username,
                password);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Registration successful! You can now log in.");

        return "redirect:/login";

    } catch (IllegalArgumentException ex) {
        model.addAttribute(
                "errorMessage",
                ex.getMessage());

        return "register";
    }
}
}