package com.example.couriertracking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.couriertracking.entity.AppUser;
import com.example.couriertracking.repository.AppUserRepository;

@Configuration
public class UserDataInitializer {

    @Bean
    public CommandLineRunner initializeUsers(
            AppUserRepository repository,
            PasswordEncoder passwordEncoder,
            @Value("${app.security.admin-password:ChangeMeAdmin123!}")
            String adminPassword,
            @Value("${app.security.staff-password:ChangeMeStaff123!}")
            String staffPassword) {

        return args -> {

            if (!repository.existsByUsername("admin")) {

                AppUser admin = new AppUser(
                        "admin",
                        passwordEncoder.encode(adminPassword),
                        "ADMIN",
                        true
                );

                repository.save(admin);
            }

            if (!repository.existsByUsername("staff")) {

                AppUser staff = new AppUser(
                        "staff",
                        passwordEncoder.encode(staffPassword),
                        "STAFF",
                        true
                );

                repository.save(staff);
            }
        };
    }
}