package com.example.couriertracking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.couriertracking.entity.Parcel;
import com.example.couriertracking.repository.ParcelRepository;

@Service
public class DeliveryPerformanceService {

private final ParcelRepository parcelRepository;

public DeliveryPerformanceService(
        ParcelRepository parcelRepository) {
    this.parcelRepository = parcelRepository;
}

@Transactional(readOnly = true)
public long getTotalParcels() {
    return parcelRepository.count();
}

@Transactional(readOnly = true)
public long getDeliveredParcels() {
    return parcelRepository.countByStatus(
            Parcel.ParcelStatus.DELIVERED);
}

@Transactional(readOnly = true)
public long getInTransitParcels() {
    return parcelRepository.countByStatus(
            Parcel.ParcelStatus.IN_TRANSIT);
}

@Transactional(readOnly = true)
public long getOutForDeliveryParcels() {
    return parcelRepository.countByStatus(
            Parcel.ParcelStatus.OUT_FOR_DELIVERY);
}

@Transactional(readOnly = true)
public long getFailedDeliveries() {
    return parcelRepository.countByStatus(
            Parcel.ParcelStatus.DELIVERY_FAILED);
}

@Transactional(readOnly = true)
public double getDeliverySuccessRate() {

    long total = getTotalParcels();

    if (total == 0) {
        return 0.0;
    }

    long delivered = getDeliveredParcels();

    return (delivered * 100.0) / total;
}

@Transactional(readOnly = true)
public long getStaffDeliveredParcels(Long staffId) {
    return parcelRepository.countByAssignedStaff_IdAndStatus(
            staffId,
            Parcel.ParcelStatus.DELIVERED);
}

@Transactional(readOnly = true)
public long getStaffInTransitParcels(Long staffId) {

    return parcelRepository.countByAssignedStaff_IdAndStatus(
            staffId,
            Parcel.ParcelStatus.IN_TRANSIT);
}

@Transactional(readOnly = true)
public long getStaffOutForDeliveryParcels(Long staffId) {

    return parcelRepository.countByAssignedStaff_IdAndStatus(
            staffId,
            Parcel.ParcelStatus.OUT_FOR_DELIVERY);
}

@Transactional(readOnly = true)
public long getStaffFailedDeliveries(Long staffId) {

    return parcelRepository.countByAssignedStaff_IdAndStatus(
            staffId,
            Parcel.ParcelStatus.DELIVERY_FAILED);
}
}