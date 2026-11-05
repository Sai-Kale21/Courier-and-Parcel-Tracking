package com.example.couriertracking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.couriertracking.entity.Customer;
import com.example.couriertracking.service.CustomerService;

@Controller
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public String listCustomers(Model model) {

        model.addAttribute("customers",
                customerService.getAllCustomers());

        return "customers/customer-list";
    }

    @GetMapping("/customers/new")
    public String showCustomerForm(Model model) {

        model.addAttribute("customer", new Customer());

        return "customers/customer-form";
    }

    @PostMapping("/customers/save")
    public String saveCustomer(
            @Validated @ModelAttribute("customer") Customer customer,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            return "customers/customer-form";
        }

        try {
            customerService.saveCustomer(customer);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Customer registered successfully!");

            return "redirect:/customers";

        } catch (IllegalArgumentException ex) {

            model.addAttribute("errorMessage", ex.getMessage());

            return "customers/customer-form";
        }
    }

    @GetMapping("/customers/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            model.addAttribute(
                    "customer",
                    customerService.getCustomerById(id));

            return "customers/customer-form";

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage());

            return "redirect:/customers";
        }
    }

    @PostMapping("/customers/update/{id}")
    public String updateCustomer(
            @PathVariable Long id,
            @Validated @ModelAttribute("customer") Customer customer,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            return "customers/customer-form";
        }

        try {
            customerService.updateCustomer(id, customer);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Customer updated successfully!");

            return "redirect:/customers";

        } catch (IllegalArgumentException ex) {

            model.addAttribute("errorMessage", ex.getMessage());

            return "customers/customer-form";
        }
    }

    @PostMapping("/customers/delete/{id}")
    public String deleteCustomer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            customerService.deleteCustomer(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Customer deleted successfully!");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage());
        }

        return "redirect:/customers";
    }
}