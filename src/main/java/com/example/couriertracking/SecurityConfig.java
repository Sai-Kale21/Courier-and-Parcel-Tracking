package com.example.couriertracking;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.couriertracking.service.CustomAuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {
	
	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    
	public SecurityConfig(
	        CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
	    this.customAuthenticationSuccessHandler =
	            customAuthenticationSuccessHandler;
	}
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
            		
            		.requestMatchers(
            		        "/customer-dashboard",
            		        "/my-parcels",
            		        "/customer-booking",
            		        "/customer-profile",
            		        "/customer-parcels/**",
            		        "/customer-profile/**"
            		).hasRole("CUSTOMER")
                // Public access
                .requestMatchers(
                    "/login",
                    "/register",
                    "/track",
                    "/error",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()

                // Admin-only customer management
                .requestMatchers("/customers/**").hasRole("ADMIN")
                    
                .requestMatchers("/delivery-staff/**").hasRole("ADMIN")
                
                .requestMatchers("/staff-workload/**").hasRole("ADMIN")
                    
                .requestMatchers("/parcels/*/assign").hasRole("ADMIN")

                // Admin and staff access
                .requestMatchers("/dashboard","/parcels/**","/reports","/reports/**").hasAnyRole("ADMIN", "STAFF")

                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(customAuthenticationSuccessHandler)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }
}