package com.example.couriertracking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import com.example.couriertracking.entity.DeliveryStaff;
import com.example.couriertracking.service.DeliveryStaffService;

@Controller
@RequestMapping("/delivery-staff")
public class DeliveryStaffController {

    private final DeliveryStaffService service;

    public DeliveryStaffController(DeliveryStaffService service) {
        this.service = service;
    }

    @GetMapping
    public String listStaff(Model model) {
        model.addAttribute("staffList", service.getAllStaff());
        return "delivery-staff/staff-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("staff", new DeliveryStaff());
        model.addAttribute("formTitle", "Register Delivery Staff");
        model.addAttribute("formAction", "/delivery-staff/save");
        return "delivery-staff/staff-form";
    }

    @PostMapping("/save")
    public String saveStaff(
            @Valid @ModelAttribute("staff") DeliveryStaff staff,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Register Delivery Staff");
            model.addAttribute("formAction", "/delivery-staff/save");
            return "delivery-staff/staff-form";
        }

        try {
            service.saveStaff(staff);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Delivery staff registered successfully.");

            return "redirect:/delivery-staff";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("formTitle", "Register Delivery Staff");
            model.addAttribute("formAction", "/delivery-staff/save");
            model.addAttribute("errorMessage", ex.getMessage());
            return "delivery-staff/staff-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        model.addAttribute("staff", service.getStaffById(id));
        model.addAttribute("formTitle", "Edit Delivery Staff");
        model.addAttribute(
                "formAction",
                "/delivery-staff/update/" + id);

        return "delivery-staff/staff-form";
    }

    @PostMapping("/update/{id}")
    public String updateStaff(
            @PathVariable Long id,
            @Valid @ModelAttribute("staff") DeliveryStaff staff,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Edit Delivery Staff");
            model.addAttribute(
                    "formAction",
                    "/delivery-staff/update/" + id);

            return "delivery-staff/staff-form";
        }

        try {
            service.updateStaff(id, staff);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Delivery staff updated successfully.");

            return "redirect:/delivery-staff";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("formTitle", "Edit Delivery Staff");
            model.addAttribute(
                    "formAction",
                    "/delivery-staff/update/" + id);
            model.addAttribute("errorMessage", ex.getMessage());

            return "delivery-staff/staff-form";
        }
    }

    @PostMapping("/toggle-active/{id}")
    public String toggleActive(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        DeliveryStaff staff = service.toggleActive(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                staff.isActive()
                        ? "Staff member activated successfully."
                        : "Staff member deactivated successfully.");

        return "redirect:/delivery-staff";
    }
}
