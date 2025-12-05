package com.flightmanagement.security;

import com.flightmanagement.entity.Account;
import com.flightmanagement.enums.AccountType;
import com.flightmanagement.enums.EmployeeType;
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

@Data
@Getter
@Setter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private Integer id;
    private String email;
    private String password;
    private String accountName;
    private AccountType accountType;
    private EmployeeType employeeType;
    private Integer score;
    private String phoneNumber;
    private String citizenId;
    private Collection<? extends GrantedAuthority> authorities;

    public static CustomUserDetails create(Account account) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (account.getAccountType() == AccountType.CUSTOMER) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        } else if (account.getAccountType() == AccountType.EMPLOYEE) {
            authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
            if (account.getEmployee() != null && account.getEmployee().getEmployeeType() != null) {
                // Use the enum's built-in authority method for type safety
                authorities.add(account.getEmployee().getEmployeeType().getAuthority());
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
