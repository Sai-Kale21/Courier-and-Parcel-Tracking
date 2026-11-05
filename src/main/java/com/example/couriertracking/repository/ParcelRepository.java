package com.example.couriertracking.repository;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.couriertracking.entity.Parcel;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {

    Optional<Parcel> findByTrackingNumber(String trackingNumber);

    boolean existsByTrackingNumber(String trackingNumber);

    List<Parcel> findAllByOrderByBookingDateDesc();

    long countByStatus(Parcel.ParcelStatus status);

    List<Parcel> findAllByOrderByBookingDateDesc(Pageable pageable);

    @Query("""
        SELECT p FROM Parcel p
        WHERE (:trackingNumber IS NULL
               OR LOWER(p.trackingNumber) LIKE LOWER(
                   CONCAT('%', :trackingNumber, '%')))
          AND (:status IS NULL OR p.status = :status)
          AND (:courierType IS NULL OR p.courierType = :courierType)
          AND (:fromDate IS NULL OR p.bookingDate >= :fromDate)
          AND (:toDate IS NULL OR p.bookingDate <= :toDate)
        ORDER BY p.bookingDate DESC
        """)
    
    List<Parcel> searchParcels(
            @Param("trackingNumber") String trackingNumber,
            @Param("status") Parcel.ParcelStatus status,
            @Param("courierType") Parcel.CourierType courierType,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);
    
    List<Parcel> findByAssignedStaff_IdOrderByBookingDateDesc(Long staffId);
    List<Parcel> findBySender_IdOrderByBookingDateDesc(Long customerId);
    List<Parcel> findByAssignedStaffIsNullOrderByBookingDateDesc();

    long countByAssignedStaff_Id(Long staffId);
    
    long countByAssignedStaffIsNotNull();
    
    long countByAssignedStaff_IdAndStatus(
    		Long staffId,
    		Parcel.ParcelStatus status
    		);
    
    long countBySender_Id(Long customerId);

    long countBySender_IdAndStatus(
            Long customerId,
            Parcel.ParcelStatus status);

}