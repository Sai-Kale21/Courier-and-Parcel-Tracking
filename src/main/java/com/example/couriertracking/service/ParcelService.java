package com.example.couriertracking.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.couriertracking.entity.Customer;
import com.example.couriertracking.entity.Parcel;
import com.example.couriertracking.entity.TrackingHistory;
import com.example.couriertracking.repository.CustomerRepository;
import com.example.couriertracking.repository.ParcelRepository;
import com.example.couriertracking.repository.TrackingHistoryRepository;

@Service
@Transactional
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final CustomerRepository customerRepository;
    private final TrackingHistoryRepository trackingHistoryRepository;

    public ParcelService(
            ParcelRepository parcelRepository,
            CustomerRepository customerRepository,
            TrackingHistoryRepository trackingHistoryRepository) {

        this.parcelRepository = parcelRepository;
        this.customerRepository = customerRepository;
        this.trackingHistoryRepository = trackingHistoryRepository;
    }

    public Parcel bookParcel(Long senderId, Parcel parcel) {

        Customer sender = customerRepository.findById(senderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Sender customer not found."));

        if (parcel.getCourierType() == null) {
            throw new IllegalArgumentException(
                    "Please select a courier type.");
        }

        if (parcel.getWeight() == null
                || parcel.getWeight().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Parcel weight must be greater than zero.");
        }

        parcel.setSender(sender);

        parcel.setTrackingNumber(generateTrackingNumber());

        parcel.setCharge(calculateCharge(
                parcel.getWeight(),
                parcel.getCourierType()));

        parcel.setStatus(Parcel.ParcelStatus.BOOKED);

        Parcel savedParcel = parcelRepository.save(parcel);

        TrackingHistory history = new TrackingHistory();

        history.setParcel(savedParcel);
        history.setStatus(Parcel.ParcelStatus.BOOKED);
        history.setLocation(savedParcel.getPickupAddress());
        history.setDescription("Parcel booking created successfully.");

        trackingHistoryRepository.save(history);

        return savedParcel;
    }

    @Transactional(readOnly = true)
    public List<Parcel> getAllParcels() {
        return parcelRepository.findAllByOrderByBookingDateDesc();
    }

    @Transactional(readOnly = true)
    public Parcel getParcelById(Long id) {

        return parcelRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Parcel not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Parcel trackParcel(String trackingNumber) {

        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Tracking number is required.");
        }

        String normalizedNumber = trackingNumber.trim()
                .toUpperCase(Locale.ROOT);

        return parcelRepository.findByTrackingNumber(normalizedNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No parcel found for tracking number: "
                                        + normalizedNumber));
    }

    private String generateTrackingNumber() {

        String trackingNumber;

        do {
            trackingNumber = "CTRK"
                    + UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 16)
                            .toUpperCase(Locale.ROOT);

        } while (parcelRepository.existsByTrackingNumber(trackingNumber));

        return trackingNumber;
    }

    private BigDecimal calculateCharge(
            BigDecimal weight,
            Parcel.CourierType courierType) {

        BigDecimal ratePerKg;
        BigDecimal baseCharge;

        if (courierType == Parcel.CourierType.EXPRESS) {
            ratePerKg = new BigDecimal("80.00");
            baseCharge = new BigDecimal("100.00");
        } else {
            ratePerKg = new BigDecimal("40.00");
            baseCharge = new BigDecimal("50.00");
        }

        return baseCharge
                .add(weight.multiply(ratePerKg))
                .setScale(2, RoundingMode.HALF_UP);
    }
}