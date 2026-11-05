package com.example.couriertracking.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.couriertracking.entity.DeliveryStaff;
import com.example.couriertracking.entity.Parcel;
import com.example.couriertracking.repository.DeliveryStaffRepository;
import com.example.couriertracking.repository.ParcelRepository;

@Service
public class StaffWorkloadService {

private final DeliveryStaffRepository deliveryStaffRepository;
private final ParcelRepository parcelRepository;

public StaffWorkloadService(
        DeliveryStaffRepository deliveryStaffRepository,
        ParcelRepository parcelRepository) {

    this.deliveryStaffRepository = deliveryStaffRepository;
    this.parcelRepository = parcelRepository;
}

// Retrieve all staff members
@Transactional(readOnly = true)
public List<DeliveryStaff> getAllStaff() {
    return deliveryStaffRepository.findAllByOrderByFullNameAsc();
}

// Retrieve all parcels assigned to a staff member
@Transactional(readOnly = true)
public List<Parcel> getAssignedParcels(Long staffId) {

    if (!deliveryStaffRepository.existsById(staffId)) {
        throw new IllegalArgumentException(
                "Delivery staff not found with ID: " + staffId);
    }

    return parcelRepository
            .findByAssignedStaff_IdOrderByBookingDateDesc(staffId);
}

// Retrieve parcels without assigned staff
@Transactional(readOnly = true)
public List<Parcel> getUnassignedParcels() {
    return parcelRepository
            .findByAssignedStaffIsNullOrderByBookingDateDesc();
}

// Count parcels assigned to a staff member
@Transactional(readOnly = true)
public long getAssignedParcelCount(Long staffId) {

    return parcelRepository.countByAssignedStaff_Id(staffId);
}

// Count all assigned parcels
@Transactional(readOnly = true)
public long getTotalAssignedParcels() {
    return parcelRepository.countByAssignedStaffIsNotNull();
}
}