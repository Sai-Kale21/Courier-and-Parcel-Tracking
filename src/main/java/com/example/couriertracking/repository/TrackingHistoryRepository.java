package com.example.couriertracking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.couriertracking.entity.TrackingHistory;

public interface TrackingHistoryRepository
        extends JpaRepository<TrackingHistory, Long> {

    List<TrackingHistory> findByParcel_IdOrderByUpdateTimeAsc(Long parcelId);

}