package com.example.couriertracking.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.example.couriertracking.entity.DeliveryStaff;

@Entity
@Table(name = "parcels")
public class Parcel {

    public enum CourierType {
        STANDARD,
        EXPRESS
    }

    public enum ParcelStatus {
        BOOKED,
        PICKED_UP,
        IN_TRANSIT,
        OUT_FOR_DELIVERY,
        DELIVERED,
        DELIVERY_FAILED,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_number",
            nullable = false,
            unique = true,
            length = 30)
    private String trackingNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private Customer sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_staff_id")
    private DeliveryStaff assignedStaff;

    @NotBlank(message = "Receiver name is required")
    @Size(max = 100, message = "Receiver name cannot exceed 100 characters")
    @Column(name = "receiver_name", nullable = false, length = 100)
    private String receiverName;

    @NotBlank(message = "Receiver phone is required")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    @NotBlank(message = "Pickup address is required")
    @Column(name = "pickup_address", nullable = false, length = 500)
    private String pickupAddress;

    @NotBlank(message = "Delivery address is required")
    @Column(name = "delivery_address", nullable = false, length = 500)
    private String deliveryAddress;

    @NotNull(message = "Parcel weight is required")
    @DecimalMin(value = "0.01", message = "Weight must be greater than zero")
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal weight;

    @NotNull(message = "Please select a courier type")
    @Enumerated(EnumType.STRING)
    @Column(name = "courier_type", nullable = false, length = 20)
    private CourierType courierType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal charge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ParcelStatus status = ParcelStatus.BOOKED;

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    public Parcel() {
    }

    @PrePersist
    public void prePersist() {
        if (bookingDate == null) {
            bookingDate = LocalDateTime.now();
        }

        if (status == null) {
            status = ParcelStatus.BOOKED;
        }
    }

    public Long getId() {
        return id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Customer getSender() {
        return sender;
    }

    public void setSender(Customer sender) {
        this.sender = sender;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public CourierType getCourierType() {
        return courierType;
    }

    public void setCourierType(CourierType courierType) {
        this.courierType = courierType;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public void setStatus(ParcelStatus status) {
        this.status = status;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public Customer getSenderForDisplay() {
        return sender;
    }
   
    public DeliveryStaff getAssignedStaff() {
        return assignedStaff;
    }

    public void setAssignedStaff(DeliveryStaff assignedStaff) {
        this.assignedStaff = assignedStaff;
    }
}