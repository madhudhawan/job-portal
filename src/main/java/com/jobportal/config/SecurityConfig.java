package com.jobportal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.jobportal.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // ==============================
    // Register BCryptPasswordEncoder as Bean
    // ==============================
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ==============================
    // Security filter chain
    // Spring Security 7.x way — no DaoAuthenticationProvider needed
    // ==============================
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            // Tell Spring Security which UserDetailsService to use
            .userDetailsService(customUserDetailsService)

            .authorizeHttpRequests(auth -> auth

                // Open pages — no login needed
                .requestMatchers(
                    "/register",
                    "/login",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()

                // Only RECRUITER can access these
                .requestMatchers("/recruiter/**").hasRole("RECRUITER")

                // Only CANDIDATE can access these
                .requestMatchers("/candidate/**").hasRole("CANDIDATE")

                // Everything else needs login
                .anyRequest().authenticated()
            )

            // Our custom login page settings
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )

            // Logout settings
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .permitAll()
            );

        return http.build();
    }
}