package com.flightmanagement.mapper;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.flightmanagement.dto.UserDetailsDto;
import com.flightmanagement.enums.AccountType;
import com.flightmanagement.security.CustomUserDetails;

@Component
public class AuthMapper {
    private String resolveRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_EMPLOYEE_")) // Prioritize specific roles
                .findFirst()
                .or(() -> authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .findFirst()
                )
                .map(auth -> auth.replace("ROLE_", ""))
                .orElse("USER");
    }

    public String getAccountTypeName(AccountType accountType) {
        return accountType == AccountType.CUSTOMER ? "Customer" : "Employee";
    }

    public UserDetailsDto toUserDetailsDto(CustomUserDetails userDetails) {
        return new UserDetailsDto(
                userDetails.getId(),
                userDetails.getAccountName(),
                userDetails.getEmail(),
                resolveRole(userDetails.getAuthorities()),
                getAccountTypeName(userDetails.getAccountType()));
    }
}