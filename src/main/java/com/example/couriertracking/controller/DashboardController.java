package com.example.couriertracking.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.couriertracking.entity.Parcel;
import com.example.couriertracking.repository.CustomerRepository;
import com.example.couriertracking.repository.ParcelRepository;

@Controller
public class DashboardController {

    private final CustomerRepository customerRepository;
    private final ParcelRepository parcelRepository;

    public DashboardController(
            CustomerRepository customerRepository,
            ParcelRepository parcelRepository) {

        this.customerRepository = customerRepository;
        this.parcelRepository = parcelRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        // Total registered customers
        long totalCustomers = customerRepository.count();

        // Total booked parcels
        long totalParcels = parcelRepository.count();

        // Parcel counts by status
        long bookedParcels =
                parcelRepository.countByStatus(
                        Parcel.ParcelStatus.BOOKED);

        long pickedUpParcels =
                parcelRepository.countByStatus(
                        Parcel.ParcelStatus.PICKED_UP);

        long inTransitParcels =
                parcelRepository.countByStatus(
                        Parcel.ParcelStatus.IN_TRANSIT);

        long outForDeliveryParcels =
                parcelRepository.countByStatus(
                        Parcel.ParcelStatus.OUT_FOR_DELIVERY);

        long deliveredParcels =
                parcelRepository.countByStatus(
                        Parcel.ParcelStatus.DELIVERED);

        long failedDeliveries =
                parcelRepository.countByStatus(
                        Parcel.ParcelStatus.DELIVERY_FAILED);

        long cancelledParcels =
                parcelRepository.countByStatus(
                        Parcel.ParcelStatus.CANCELLED);

        List<Parcel> recentParcels =
                parcelRepository.findAllByOrderByBookingDateDesc(
                        PageRequest.of(0, 5));

        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalParcels", totalParcels);

        model.addAttribute("bookedParcels", bookedParcels);
        model.addAttribute("pickedUpParcels", pickedUpParcels);
        model.addAttribute("inTransitParcels", inTransitParcels);
        model.addAttribute("outForDeliveryParcels",
                outForDeliveryParcels);
        model.addAttribute("deliveredParcels", deliveredParcels);
        model.addAttribute("failedDeliveries", failedDeliveries);
        model.addAttribute("cancelledParcels", cancelledParcels);

        model.addAttribute("recentParcels", recentParcels);

        return "dashboard";
    }
}