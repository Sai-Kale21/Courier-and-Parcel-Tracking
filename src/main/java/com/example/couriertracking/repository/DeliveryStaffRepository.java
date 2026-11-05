package com.example.couriertracking.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.couriertracking.entity.DeliveryStaff;

public interface DeliveryStaffRepository
        extends JpaRepository<DeliveryStaff, Long> {

    Optional<DeliveryStaff> findByEmail(String email);

    Optional<DeliveryStaff> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<DeliveryStaff> findByActiveTrueOrderByFullNameAsc();

    List<DeliveryStaff> findByActiveFalseOrderByFullNameAsc();

    List<DeliveryStaff> findAllByOrderByFullNameAsc();
}