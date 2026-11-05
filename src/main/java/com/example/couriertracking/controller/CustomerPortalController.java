package com.example.couriertracking.controller;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.couriertracking.entity.AppUser;
import com.example.couriertracking.entity.Parcel;
import com.example.couriertracking.repository.AppUserRepository;
import com.example.couriertracking.repository.ParcelRepository;

import com.example.couriertracking.service.CustomerService;
import com.example.couriertracking.service.ParcelService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.couriertracking.entity.Customer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CustomerPortalController {

    private final AppUserRepository appUserRepository;
    private final ParcelRepository parcelRepository;
    private final ParcelService parcelService;
    private final CustomerService customerService;

    public CustomerPortalController(
            AppUserRepository appUserRepository,
            ParcelRepository parcelRepository,
            ParcelService parcelService,
            CustomerService customerService) {

        this.appUserRepository = appUserRepository;
        this.parcelRepository = parcelRepository;
        this.parcelService = parcelService;
        this.customerService = customerService;
    }

    @GetMapping("/customer-dashboard")
    public String customerDashboard(
            Authentication authentication,
            Model model) {

        AppUser appUser = appUserRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer account not found."));

        if (!"CUSTOMER".equals(appUser.getRole())
                || appUser.getCustomer() == null) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "This account is not linked to a customer.");
        }

        Long customerId = appUser.getCustomer().getId();

        model.addAttribute(
                "customer",
                appUser.getCustomer());

        model.addAttribute(
                "totalParcels",
                parcelRepository.countBySender_Id(customerId));

        model.addAttribute(
                "bookedParcels",
                parcelRepository.countBySender_IdAndStatus(
                        customerId,
                        Parcel.ParcelStatus.BOOKED));

        model.addAttribute(
                "inTransitParcels",
                parcelRepository.countBySender_IdAndStatus(
                        customerId,
                        Parcel.ParcelStatus.IN_TRANSIT));

        model.addAttribute(
                "outForDeliveryParcels",
                parcelRepository.countBySender_IdAndStatus(
                        customerId,
                        Parcel.ParcelStatus.OUT_FOR_DELIVERY));

        model.addAttribute(
                "deliveredParcels",
                parcelRepository.countBySender_IdAndStatus(
                        customerId,
                        Parcel.ParcelStatus.DELIVERED));

        return "customer-dashboard";
    }
    
    @GetMapping("/my-parcels")
    public String showMyParcels(
            Authentication authentication,
            Model model) {

        AppUser appUser = appUserRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User account not found."));

        if (!"CUSTOMER".equals(appUser.getRole())
                || appUser.getCustomer() == null) {

            throw new org.springframework.security.access.AccessDeniedException(
                    "This account is not linked to a customer.");
        }

        Long customerId = appUser.getCustomer().getId();

        model.addAttribute(
                "customer",
                appUser.getCustomer());

        model.addAttribute(
                "parcels",
                parcelRepository.findBySender_IdOrderByBookingDateDesc(
                        customerId));

        return "my-parcels";
    }
    
    @GetMapping("/customer-booking")
    public String showCustomerBookingForm(Model model) {

        model.addAttribute("parcel", new Parcel());

        model.addAttribute(
                "courierTypes",
                Parcel.CourierType.values());

        return "customer-booking";
    }
    
    @PostMapping("/customer-booking")
    public String bookCustomerParcel(
            Authentication authentication,
            @RequestParam String receiverName,
            @RequestParam String receiverPhone,
            @RequestParam String pickupAddress,
            @RequestParam String deliveryAddress,
            @RequestParam java.math.BigDecimal weight,
            @RequestParam Parcel.CourierType courierType,
            RedirectAttributes redirectAttributes) {

        try {
            AppUser appUser = appUserRepository
                    .findByUsername(authentication.getName())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "User account not found."));

            if (!"CUSTOMER".equals(appUser.getRole())
                    || appUser.getCustomer() == null) {

                throw new org.springframework.security.access.AccessDeniedException(
                        "This account is not linked to a customer.");
            }

            Parcel parcel = new Parcel();

            parcel.setReceiverName(receiverName);
            parcel.setReceiverPhone(receiverPhone);
            parcel.setPickupAddress(pickupAddress);
            parcel.setDeliveryAddress(deliveryAddress);
            parcel.setWeight(weight);
            parcel.setCourierType(courierType);

            Long customerId = appUser.getCustomer().getId();

            Parcel savedParcel = parcelService.bookParcel(
                    customerId,
                    parcel);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Parcel booked successfully! Tracking Number: "
                            + savedParcel.getTrackingNumber());

            return "redirect:/my-parcels";

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage());

            return "redirect:/customer-booking";
        }
    }
    
    @GetMapping("/customer-profile")
    public String viewCustomerProfile(
            Authentication authentication,
            Model model) {

        AppUser appUser = appUserRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User account not found."));

        if (!"CUSTOMER".equals(appUser.getRole())
                || appUser.getCustomer() == null) {

            throw new org.springframework.security.access.AccessDeniedException(
                    "This account is not linked to a customer.");
        }

        model.addAttribute("customer", appUser.getCustomer());

        return "customer-profile";
    }
    
    @PostMapping("/customer-profile/update")
    public String updateCustomerProfile(
            Authentication authentication,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address,
            RedirectAttributes redirectAttributes) {

        try {
            AppUser appUser = appUserRepository
                    .findByUsername(authentication.getName())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "User account not found."));

            if (!"CUSTOMER".equals(appUser.getRole())
                    || appUser.getCustomer() == null) {

                throw new org.springframework.security.access.AccessDeniedException(
                        "This account is not linked to a customer.");
            }

            Customer customer = appUser.getCustomer();

            customerService.updateCustomerProfile(
                    customer.getId(),
                    name,
                    email,
                    phone,
                    address);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Your profile was updated successfully.");

            return "redirect:/customer-profile";

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage());

            return "redirect:/customer-profile";
        }
    }
}