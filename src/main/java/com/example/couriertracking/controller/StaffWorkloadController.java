package com.example.couriertracking.controller;

import java.util.HashMap;
import java.util.Map;

import com.example.couriertracking.entity.DeliveryStaff;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.couriertracking.service.StaffWorkloadService;

@Controller
public class StaffWorkloadController {

private final StaffWorkloadService staffWorkloadService;

public StaffWorkloadController(
        StaffWorkloadService staffWorkloadService) {

    this.staffWorkloadService = staffWorkloadService;
}

// Display the workload dashboard
@GetMapping("/staff-workload")
public String showWorkload(Model model) {

var staffList = staffWorkloadService.getAllStaff();

Map<Long, Long> parcelCounts = new HashMap<>();

for (DeliveryStaff staff : staffList) {
    parcelCounts.put(
            staff.getId(),
            staffWorkloadService.getAssignedParcelCount(staff.getId())
    );
}

var unassignedParcels =
        staffWorkloadService.getUnassignedParcels();

model.addAttribute("staffList", staffList);

model.addAttribute("parcelCounts", parcelCounts);

model.addAttribute(
        "totalAssignedParcels",
        staffWorkloadService.getTotalAssignedParcels()
);

model.addAttribute("unassignedParcels", unassignedParcels);

model.addAttribute(
        "unassignedCount",
        unassignedParcels.size()
);
return "staff-workload/workload-dashboard";
}

// Display parcels assigned to a specific staff member
@GetMapping("/staff-workload/{staffId}/parcels")
public String showStaffParcels(
        @PathVariable Long staffId,
        Model model,
        RedirectAttributes redirectAttributes) {

    try {
        model.addAttribute(
                "staffParcels",
                staffWorkloadService.getAssignedParcels(staffId));

        model.addAttribute(
                "staffId",
                staffId);

        model.addAttribute(
                "staff",
                staffWorkloadService.getAllStaff()
                        .stream()
                        .filter(s -> s.getId().equals(staffId))
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Delivery staff not found.")));

        return "staff-workload/staff-parcels";

    } catch (IllegalArgumentException e) {

        redirectAttributes.addFlashAttribute(
                "errorMessage",
                e.getMessage());

        return "redirect:/staff-workload";
    }
}
}