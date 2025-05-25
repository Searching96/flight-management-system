package com.flightmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173", "http://localhost:4173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "X-Requested-With"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny())
                .contentTypeOptions(contentTypeOptions -> {})
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/accounts/login", "/api/accounts/register").permitAll()
                .requestMatchers("/api/flights/**", "/api/airports/**", "/api/ticket-classes/**").permitAll()
                .requestMatchers("/api/planes/**", "/api/passengers/**", "/api/tickets/**").permitAll()
                .requestMatchers("/api/chatboxes/**", "/api/messages/**").permitAll()
                .requestMatchers("/api/customers/**", "/api/employees/**").permitAll()
                .requestMatchers("/api/flight-details/**", "/api/flight-ticket-classes/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/parameters/**").permitAll() // Allow for demo
                .anyRequest().permitAll() // Permit all for demonstration
            );
        
        return http.build();
    }
}
