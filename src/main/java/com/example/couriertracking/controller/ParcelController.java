package com.example.couriertracking.controller;

import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.couriertracking.entity.Parcel;
import com.example.couriertracking.entity.TrackingHistory;
import com.example.couriertracking.repository.CustomerRepository;
import com.example.couriertracking.service.ParcelService;
import com.example.couriertracking.service.TrackingService;

@Controller
public class ParcelController {

    private final ParcelService parcelService;
    private final CustomerRepository customerRepository;
    private final TrackingService trackingService;

    public ParcelController(
            ParcelService parcelService,
            CustomerRepository customerRepository,
            TrackingService trackingService) {

        this.parcelService = parcelService;
        this.customerRepository = customerRepository;
        this.trackingService = trackingService;
    }

    @GetMapping("/parcels")
    public String listParcels(Model model) {

        model.addAttribute(
                "parcels",
                parcelService.getAllParcels());

        return "parcels/parcel-list";
    }

    @GetMapping("/parcels/new")
    public String showBookingForm(Model model) {

        model.addAttribute("parcel", new Parcel());

        model.addAttribute(
                "customers",
                customerRepository.findAll());

        model.addAttribute(
                "courierTypes",
                Parcel.CourierType.values());

        return "parcels/parcel-form";
    }

    @PostMapping("/parcels/book")
    public String bookParcel(
            @Validated @ModelAttribute("parcel") Parcel parcel,
            BindingResult result,
            @RequestParam("senderId") Long senderId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {

            model.addAttribute(
                    "customers",
                    customerRepository.findAll());

            model.addAttribute(
                    "courierTypes",
                    Parcel.CourierType.values());

            return "parcels/parcel-form";
        }

        try {

            Parcel savedParcel =
                    parcelService.bookParcel(senderId, parcel);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Parcel booked successfully! Tracking Number: "
                            + savedParcel.getTrackingNumber());

            return "redirect:/parcels";

        } catch (IllegalArgumentException ex) {

            model.addAttribute(
                    "errorMessage",
                    ex.getMessage());

            model.addAttribute(
                    "customers",
                    customerRepository.findAll());

            model.addAttribute(
                    "courierTypes",
                    Parcel.CourierType.values());

            return "parcels/parcel-form";
        }
    }

    @GetMapping("/parcels/view/{id}")
    public String viewParcel(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            model.addAttribute(
                    "parcel",
                    parcelService.getParcelById(id));

            return "parcels/parcel-details";

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage());

            return "redirect:/parcels";
        }
    }
    
    @GetMapping("/track")
    public String trackParcel(
            @RequestParam(required = false) String trackingNumber,
            Model model) {

        if (trackingNumber == null || trackingNumber.isBlank()) {
            return "parcels/track-parcel";
        }

        model.addAttribute("trackingNumber", trackingNumber);

        try {
            var parcel = trackingService.trackParcel(trackingNumber);

            model.addAttribute("parcel", parcel);

            model.addAttribute(
                    "history",
                    trackingService.getTrackingHistory(parcel.getId()));

        } catch (IllegalArgumentException ex) {

            model.addAttribute("errorMessage", ex.getMessage());
        }

        return "parcels/track-parcel";
    }
   
    @GetMapping("/parcels/status/{id}")
    public String showStatusForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Parcel parcel = parcelService.getParcelById(id);

            model.addAttribute("parcel", parcel);

            List<Parcel.ParcelStatus> allowedStatuses =
                    switch (parcel.getStatus()) {

                        case BOOKED -> Arrays.asList(
                                Parcel.ParcelStatus.PICKED_UP,
                                Parcel.ParcelStatus.CANCELLED);

                        case PICKED_UP -> Arrays.asList(
                                Parcel.ParcelStatus.IN_TRANSIT,
                                Parcel.ParcelStatus.DELIVERY_FAILED);

                        case IN_TRANSIT -> Arrays.asList(
                                Parcel.ParcelStatus.OUT_FOR_DELIVERY,
                                Parcel.ParcelStatus.DELIVERY_FAILED);

                        case OUT_FOR_DELIVERY -> Arrays.asList(
                                Parcel.ParcelStatus.DELIVERED,
                                Parcel.ParcelStatus.DELIVERY_FAILED);

                        case DELIVERY_FAILED -> Arrays.asList(
                                Parcel.ParcelStatus.OUT_FOR_DELIVERY,
                                Parcel.ParcelStatus.CANCELLED);

                        case DELIVERED, CANCELLED -> List.of();
                    };

            model.addAttribute("allowedStatuses", allowedStatuses);

            return "parcels/status-update";

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage());

            return "redirect:/parcels";
        }
    }


    @PostMapping("/parcels/status/{id}")
    public String updateParcelStatus(
            @PathVariable Long id,
            @RequestParam Parcel.ParcelStatus status,
            @RequestParam String location,
            @RequestParam String description,
            RedirectAttributes redirectAttributes) {

        try {

            TrackingHistory history = trackingService.updateStatus(
                    id,
                    status,
                    location,
                    description);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Parcel status updated successfully to "
                            + history.getStatus() + ".");

            return "redirect:/parcels";

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage());

            return "redirect:/parcels/status/" + id;
        }
    }
}