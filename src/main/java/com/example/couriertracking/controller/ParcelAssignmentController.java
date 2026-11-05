package com.example.couriertracking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.couriertracking.entity.Parcel;
import com.example.couriertracking.repository.DeliveryStaffRepository;
import com.example.couriertracking.repository.ParcelRepository;
import com.example.couriertracking.service.ParcelAssignmentService;

@Controller
public class ParcelAssignmentController {

private final ParcelRepository parcelRepository;
private final DeliveryStaffRepository deliveryStaffRepository;
private final ParcelAssignmentService parcelAssignmentService;

public ParcelAssignmentController(
        ParcelRepository parcelRepository,
        DeliveryStaffRepository deliveryStaffRepository,
        ParcelAssignmentService parcelAssignmentService) {

    this.parcelRepository = parcelRepository;
    this.deliveryStaffRepository = deliveryStaffRepository;
    this.parcelAssignmentService = parcelAssignmentService;
}

// Display the assignment form for a specific parcel
@GetMapping("/parcels/{id}/assign")
public String showAssignmentForm(
        @PathVariable Long id,
        Model model,
        RedirectAttributes redirectAttributes) {

    Parcel parcel = parcelRepository.findById(id).orElse(null);

    if (parcel == null) {
        redirectAttributes.addFlashAttribute(
                "errorMessage", "Parcel not found.");

        return "redirect:/parcels";
    }

    model.addAttribute("parcel", parcel);

    model.addAttribute(
            "deliveryStaffList",
            deliveryStaffRepository.findByActiveTrueOrderByFullNameAsc());

    return "parcels/assign-staff";
}

// Assign a parcel to a delivery staff member
@PostMapping("/parcels/{id}/assign")
public String assignStaff(
        @PathVariable Long id,
        @RequestParam Long staffId,
        RedirectAttributes redirectAttributes) {

    try {
        parcelAssignmentService.assignStaff(id, staffId);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Delivery staff assigned successfully!");

    } catch (IllegalArgumentException e) {

        redirectAttributes.addFlashAttribute(
                "errorMessage", e.getMessage());

        return "redirect:/parcels/" + id + "/assign";
    }

    return "redirect:/parcels";
}

}
