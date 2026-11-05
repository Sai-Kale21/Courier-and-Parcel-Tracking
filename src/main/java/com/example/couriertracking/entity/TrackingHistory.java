package com.example.couriertracking.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tracking_history")
public class TrackingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parcel_id", nullable = false)
    private Parcel parcel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Parcel.ParcelStatus status;

    @NotBlank(message = "Location is required")
    @Size(max = 150, message = "Location cannot exceed 150 characters")
    @Column(nullable = false, length = 150)
    private String location;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    public TrackingHistory() {
    }

    @PrePersist
    public void prePersist() {
        if (updateTime == null) {
            updateTime = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Parcel getParcel() {
        return parcel;
    }

    public void setParcel(Parcel parcel) {
        this.parcel = parcel;
    }

    public Parcel.ParcelStatus getStatus() {
        return status;
    }

    public void setStatus(Parcel.ParcelStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
}