package com.example.couriertracking.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.couriertracking.entity.Parcel;
import com.example.couriertracking.entity.TrackingHistory;
import com.example.couriertracking.repository.ParcelRepository;
import com.example.couriertracking.repository.TrackingHistoryRepository;

@Service
@Transactional
public class TrackingService {

    private final ParcelRepository parcelRepository;
    private final TrackingHistoryRepository trackingHistoryRepository;

    public TrackingService(
            ParcelRepository parcelRepository,
            TrackingHistoryRepository trackingHistoryRepository) {

        this.parcelRepository = parcelRepository;
        this.trackingHistoryRepository = trackingHistoryRepository;
    }

    @Transactional(readOnly = true)
    public Parcel trackParcel(String trackingNumber) {

        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Tracking number is required.");
        }

        String normalizedNumber = trackingNumber.trim();

        return parcelRepository.findByTrackingNumber(normalizedNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No parcel found with tracking number: "
                                        + normalizedNumber));
    }

    @Transactional(readOnly = true)
    public List<TrackingHistory> getTrackingHistory(Long parcelId) {

        if (!parcelRepository.existsById(parcelId)) {
            throw new IllegalArgumentException(
                    "Parcel not found.");
        }

        return trackingHistoryRepository
                .findByParcel_IdOrderByUpdateTimeAsc(parcelId);
    }

    public TrackingHistory updateStatus(
            Long parcelId,
            Parcel.ParcelStatus newStatus,
            String location,
            String description) {

        Parcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Parcel not found."));

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "Please select a parcel status.");
        }

        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException(
                    "Location is required.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Description is required.");
        }

        Parcel.ParcelStatus currentStatus = parcel.getStatus();

        boolean allowed = switch (currentStatus) {
            case BOOKED ->
                    newStatus == Parcel.ParcelStatus.PICKED_UP
                    || newStatus == Parcel.ParcelStatus.CANCELLED;

            case PICKED_UP ->
                    newStatus == Parcel.ParcelStatus.IN_TRANSIT
                    || newStatus == Parcel.ParcelStatus.DELIVERY_FAILED;

            case IN_TRANSIT ->
                    newStatus == Parcel.ParcelStatus.OUT_FOR_DELIVERY
                    || newStatus == Parcel.ParcelStatus.DELIVERY_FAILED;

            case OUT_FOR_DELIVERY ->
                    newStatus == Parcel.ParcelStatus.DELIVERED
                    || newStatus == Parcel.ParcelStatus.DELIVERY_FAILED;

            case DELIVERY_FAILED ->
                    newStatus == Parcel.ParcelStatus.OUT_FOR_DELIVERY
                    || newStatus == Parcel.ParcelStatus.CANCELLED;

            case DELIVERED, CANCELLED -> false;
        };

        if (!allowed) {
            throw new IllegalArgumentException(
                    "Invalid status transition from "
                    + currentStatus + " to " + newStatus + ".");
        }

        parcel.setStatus(newStatus);

        parcelRepository.save(parcel);

        TrackingHistory history = new TrackingHistory();

        history.setParcel(parcel);
        history.setStatus(newStatus);
        history.setLocation(location.trim());
        history.setDescription(description.trim());

        return trackingHistoryRepository.save(history);
    }
}