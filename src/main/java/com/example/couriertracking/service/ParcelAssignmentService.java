package com.example.couriertracking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.couriertracking.entity.DeliveryStaff;
import com.example.couriertracking.entity.Parcel;
import com.example.couriertracking.repository.DeliveryStaffRepository;
import com.example.couriertracking.repository.ParcelRepository;

@Service
public class ParcelAssignmentService {

    private final ParcelRepository parcelRepository;
    private final DeliveryStaffRepository deliveryStaffRepository;

    public ParcelAssignmentService(
            ParcelRepository parcelRepository,
            DeliveryStaffRepository deliveryStaffRepository) {

        this.parcelRepository = parcelRepository;
        this.deliveryStaffRepository = deliveryStaffRepository;
    }

    @Transactional
    public void assignStaff(Long parcelId, Long staffId) {

        Parcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Parcel not found with ID: " + parcelId));

        DeliveryStaff staff = deliveryStaffRepository.findById(staffId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Delivery staff not found with ID: " + staffId));

        if (!staff.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot assign a parcel to inactive delivery staff.");
        }
        parcel.setAssignedStaff(staff);
        parcelRepository.save(parcel);
    }
}