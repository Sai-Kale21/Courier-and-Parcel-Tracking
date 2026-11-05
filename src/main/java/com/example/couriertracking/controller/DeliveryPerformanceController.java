package com.example.couriertracking.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.couriertracking.entity.DeliveryStaff;
import com.example.couriertracking.entity.Parcel;
import com.example.couriertracking.repository.DeliveryStaffRepository;
import com.example.couriertracking.service.DeliveryPerformanceService;

@Controller
public class DeliveryPerformanceController {
	
private final DeliveryPerformanceService performanceService;
private final DeliveryStaffRepository deliveryStaffRepository;

public DeliveryPerformanceController(
        DeliveryPerformanceService performanceService,
        DeliveryStaffRepository deliveryStaffRepository) {

    this.performanceService = performanceService;
    this.deliveryStaffRepository = deliveryStaffRepository;
}

@GetMapping("/delivery-performance")
public String showPerformanceDashboard(Model model) {

    // Overall delivery statistics
    model.addAttribute(
            "totalParcels",
            performanceService.getTotalParcels());

    model.addAttribute(
            "deliveredParcels",
            performanceService.getDeliveredParcels());

    model.addAttribute(
            "inTransitParcels",
            performanceService.getInTransitParcels());

    model.addAttribute(
            "outForDeliveryParcels",
            performanceService.getOutForDeliveryParcels());

    model.addAttribute(
            "failedDeliveries",
            performanceService.getFailedDeliveries());

    model.addAttribute(
            "deliverySuccessRate",
            performanceService.getDeliverySuccessRate());

    // Individual staff performance
    List<StaffPerformance> staffPerformanceList = new ArrayList<>();

    List<DeliveryStaff> staffList =
            deliveryStaffRepository.findAllByOrderByFullNameAsc();

    for (DeliveryStaff staff : staffList) {

        Long staffId = staff.getId();

        long delivered =
                performanceService.getStaffDeliveredParcels(staffId);

        long inTransit =
                performanceService.getStaffInTransitParcels(staffId);

        long outForDelivery =
                performanceService.getStaffOutForDeliveryParcels(staffId);

        long failed =
                performanceService.getStaffFailedDeliveries(staffId);

        long totalAssigned =
                delivered + inTransit + outForDelivery + failed;

        // Include other assigned statuses in the total.
        // For example: BOOKED, PICKED_UP, and CANCELLED.
        // These statuses are not included in the four status counts above.
        staffPerformanceList.add(
                new StaffPerformance(
                        staff.getId(),
                        staff.getFullName(),
                        staff.getPhone(),
                        totalAssigned,
                        delivered,
                        inTransit,
                        outForDelivery,
                        failed
                )
        );
    }

    model.addAttribute(
            "staffPerformanceList",
            staffPerformanceList);

    return "delivery-performance/delivery-performance";
}

// Data structure for the staff performance table
public static class StaffPerformance {

    private final Long staffId;
    private final String fullName;
    private final String phone;
    private final long totalAssigned;
    private final long delivered;
    private final long inTransit;
    private final long outForDelivery;
    private final long failed;

    public StaffPerformance(
            Long staffId,
            String fullName,
            String phone,
            long totalAssigned,
            long delivered,
            long inTransit,
            long outForDelivery,
            long failed) {

        this.staffId = staffId;
        this.fullName = fullName;
        this.phone = phone;
        this.totalAssigned = totalAssigned;
        this.delivered = delivered;
        this.inTransit = inTransit;
        this.outForDelivery = outForDelivery;
        this.failed = failed;
    }

    public Long getStaffId() {
        return staffId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public long getTotalAssigned() {
        return totalAssigned;
    }

    public long getDelivered() {
        return delivered;
    }

    public long getInTransit() {
        return inTransit;
    }

    public long getOutForDelivery() {
        return outForDelivery;
    }

    public long getFailed() {
        return failed;
    }
}
}