package com.flightmanagement.security;

import com.flightmanagement.entity.Account;
import com.flightmanagement.entity.Employee;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(account.getEmail())
                .password(account.getPassword())
                .authorities(getAuthorities(account))
                .build();
    }

    private List<GrantedAuthority> getAuthorities(Account account) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (account.getAccountType() == 1) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        } else if (account.getAccountType() == 2) {
            authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
            Employee employee = employeeRepository.findByEmail(account.getEmail()).orElse(null);
            if (employee != null) {
                switch (employee.getEmployeeType()) {
                    case 1: authorities.add(new SimpleGrantedAuthority("BAGGAGE_HANDLER")); break;
                    case 2: authorities.add(new SimpleGrantedAuthority("CHECK_IN_STAFF")); break;
                    case 3: authorities.add(new SimpleGrantedAuthority("SECURITY_STAFF")); break;
                    case 4: authorities.add(new SimpleGrantedAuthority("FLIGHT_CREW")); break;
                    case 5:
                        authorities.add(new SimpleGrantedAuthority("ADMIN"));
                        authorities.add(new SimpleGrantedAuthority("FLIGHT_CREW"));
                        authorities.add(new SimpleGrantedAuthority("SECURITY_STAFF"));
                        authorities.add(new SimpleGrantedAuthority("CHECK_IN_STAFF"));
                        authorities.add(new SimpleGrantedAuthority("BAGGAGE_HANDLER"));
                        break;
                }
            }
        }
        return authorities;
    }
}
