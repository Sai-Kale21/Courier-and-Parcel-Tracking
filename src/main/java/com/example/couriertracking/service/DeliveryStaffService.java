package com.example.couriertracking.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.couriertracking.entity.DeliveryStaff;
import com.example.couriertracking.repository.DeliveryStaffRepository;

@Service
@Transactional
public class DeliveryStaffService {

    private final DeliveryStaffRepository repository;

    public DeliveryStaffService(DeliveryStaffRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<DeliveryStaff> getAllStaff() {
        return repository.findAllByOrderByFullNameAsc();
    }

    @Transactional(readOnly = true)
    public DeliveryStaff getStaffById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Delivery staff member not found."));
    }

    public DeliveryStaff saveStaff(DeliveryStaff staff) {

        if (repository.existsByEmail(staff.getEmail().trim())) {
            throw new IllegalArgumentException(
                    "Email address is already registered.");
        }

        if (repository.existsByPhone(staff.getPhone().trim())) {
            throw new IllegalArgumentException(
                    "Phone number is already registered.");
        }

        staff.setFullName(staff.getFullName().trim());
        staff.setEmail(staff.getEmail().trim());
        staff.setPhone(staff.getPhone().trim());
        staff.setAddress(staff.getAddress().trim());

        if (staff.getJoiningDate() == null) {
            staff.setJoiningDate(LocalDate.now());
        }

        staff.setActive(true);

        return repository.save(staff);
    }

    public DeliveryStaff updateStaff(Long id, DeliveryStaff updatedStaff) {

        DeliveryStaff existing = getStaffById(id);

        boolean emailUsed = repository.findByEmail(
                updatedStaff.getEmail().trim())
                .filter(staff -> !staff.getId().equals(id))
                .isPresent();

        if (emailUsed) {
            throw new IllegalArgumentException(
                    "Email address is already used by another staff member.");
        }

        boolean phoneUsed = repository.findByPhone(
                updatedStaff.getPhone().trim())
                .filter(staff -> !staff.getId().equals(id))
                .isPresent();

        if (phoneUsed) {
            throw new IllegalArgumentException(
                    "Phone number is already used by another staff member.");
        }

        existing.setFullName(updatedStaff.getFullName().trim());
        existing.setEmail(updatedStaff.getEmail().trim());
        existing.setPhone(updatedStaff.getPhone().trim());
        existing.setAddress(updatedStaff.getAddress().trim());
        existing.setVehicleNumber(
                updatedStaff.getVehicleNumber() == null
                        ? null
                        : updatedStaff.getVehicleNumber().trim());

        existing.setJoiningDate(updatedStaff.getJoiningDate());

        return repository.save(existing);
    }

    public DeliveryStaff toggleActive(Long id) {

        DeliveryStaff staff = getStaffById(id);
        staff.setActive(!staff.isActive());

        return repository.save(staff);
    }
}
