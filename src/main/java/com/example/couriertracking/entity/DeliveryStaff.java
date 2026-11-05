package com.example.couriertracking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "delivery_staff",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_delivery_staff_email",
            columnNames = "email"
        ),
        @UniqueConstraint(
            name = "uk_delivery_staff_phone",
            columnNames = "phone"
        )
    }
)
public class DeliveryStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Column(nullable = false, length = 20)
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Column(nullable = false, length = 150)
    private String email;

    @NotBlank(message = "Address is required")
    @Column(nullable = false, length = 500)
    private String address;

    @Size(max = 30, message = "Vehicle number cannot exceed 30 characters")
    @Column(name = "vehicle_number", length = 30)
    private String vehicleNumber;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(nullable = false)
    private boolean active = true;

    public DeliveryStaff() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}