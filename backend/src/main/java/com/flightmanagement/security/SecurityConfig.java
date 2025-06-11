package com.flightmanagement.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security Configuration with Enhanced CORS Support
 * Last updated: 2025-06-11 08:14:55 UTC by thinh0704hcm
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        System.out.println("Configuring authentication provider at 2025-06-11 08:14:55 UTC by thinh0704hcm");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Configuring security filter chain with CORS at 2025-06-11 08:14:55 UTC by thinh0704hcm");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh",
                                "/api/auth/validate-token", "/api/auth/forget-password", "/api/auth/reset-password",
                                "/api/auth/employee/register",
                                "/api/demo/**",
                                "/api/flights/search", "/api/flights/{id}",
                                "/api/airports", "/api/ticket-classes",
                                "/api/flight-ticket-classes/flight/**",
                                "/api/flight-ticket-classes/occupied-seats/**",
                                "/api/flight-ticket-classes/{flightId}/{ticketClassId}/update-remaining",
                                "/api/passengers/**",
                                "/api/tickets/confirmation-code", "/api/tickets", "/api/tickets/{id}",
                                "/api/flight-details/flight/{flightId}", "/api/parameters",
                                "/api/tickets/booking-lookup/{confirmationCode}", "/api/tickets/booking-lookup/{id}",
                                "/api/debug/login-by-name/{name}",
                                "/api/payment/create", "/api/payment/return", "/api/payment/IPN",
                                // Add OPTIONS method for all API endpoints
                                "/api/**")
                        .permitAll()

                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        System.out.println("Configuring CORS configuration source at 2025-06-11 08:14:55 UTC by thinh0704hcm");

        CorsConfiguration configuration = new CorsConfiguration();

        // Allow specific origins
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:3000"
        ));

        // Allow all HTTP methods including PATCH
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow credentials (important for JWT tokens)
        configuration.setAllowCredentials(true);

        // Set max age for preflight requests
        configuration.setMaxAge(3600L);

        // Expose headers that frontend might need
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        System.out.println("CORS configuration registered for all paths at 2025-06-11 08:14:55 UTC by thinh0704hcm");
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        System.out.println("Configuring authentication manager at 2025-06-11 08:14:55 UTC by thinh0704hcm");
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("Configuring password encoder at 2025-06-11 08:14:55 UTC by thinh0704hcm");
        return new BCryptPasswordEncoder();
    }
}