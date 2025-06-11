package com.flightmanagement.security;

import com.flightmanagement.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// CustomUserDetails.java
@Data
@Getter
@Setter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private Integer id;
    private String email;
    private String password;
    private String accountName;
    private Integer accountType;
    private Integer employeeType;
    private Integer score;
    private String phoneNumber;
    private String citizenId;
    private Collection<? extends GrantedAuthority> authorities;

    public static CustomUserDetails create(Account account) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (account.getAccountType() == 1) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        } else if (account.getAccountType() == 2) {
            authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
            if (account.getEmployee() != null) {
                switch (account.getEmployee().getEmployeeType()) {
                    case 1 -> authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE_FLIGHT_SCHEDULING"));
                    case 2 -> authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE_TICKETING"));
                    case 3 -> authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE_SUPPORT"));
                    case 4 -> authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE_ACCOUNTING"));
                    case 5 -> authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE_FLIGHT_OPERATIONS"));
                    case 6 -> authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE_HUMAN_RESOURCES"));
                    case 7 -> authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE_ADMINISTRATOR"));
                }
            }
        }

        return new CustomUserDetails(
                account.getAccountId(),
                account.getEmail(),
                account.getPassword(),
                account.getAccountName(),
                account.getAccountType(),
                account.getEmployee() != null ? account.getEmployee().getEmployeeType() : null,
                account.getCustomer() != null ? account.getCustomer().getScore() : null,
                account.getPhoneNumber(),
                account.getCitizenId(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
