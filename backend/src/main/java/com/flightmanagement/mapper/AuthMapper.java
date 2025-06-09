package com.flightmanagement.mapper;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.flightmanagement.dto.UserDetailsDto;
import com.flightmanagement.security.CustomUserDetails;

// AuthMapper.java
@Component
public class AuthMapper {
    public String resolveRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .orElse("USER");
    }

    public String getAccountTypeName(Integer accountType) {
        return accountType == 1 ? "Customer" : "Employee";
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