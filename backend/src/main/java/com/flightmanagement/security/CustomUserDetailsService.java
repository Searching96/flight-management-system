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

/**
 * Custom UserDetailsService for Spring Security
 * Loads user details and authorities based on account type and employee type
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    /**
     * Load user by email (username)
     * @param email User email address
     * @return UserDetails with authorities based on account type
     * @throws UsernameNotFoundException if user not found
     */
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
    
    /**
     * Get user authorities based on account type and employee type
     * Account Types: 1=Customer, 2=Employee
     * Employee Types: 1=Baggage, 2=Check-in, 3=Security, 4=Flight Crew, 5=Admin
     */
    private List<GrantedAuthority> getAuthorities(Account account) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Base role based on account type
        if (account.getAccountType() == 1) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        } else if (account.getAccountType() == 2) {
            authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
              // Additional authorities for employees based on employee type
            Employee employee = employeeRepository.findByEmail(account.getEmail())
                    .orElse(null);
            
            if (employee != null) {
                switch (employee.getEmployeeType()) {
                    case 1:
                        authorities.add(new SimpleGrantedAuthority("BAGGAGE_HANDLER"));
                        break;
                    case 2:
                        authorities.add(new SimpleGrantedAuthority("CHECK_IN_STAFF"));
                        break;
                    case 3:
                        authorities.add(new SimpleGrantedAuthority("SECURITY_STAFF"));
                        break;
                    case 4:
                        authorities.add(new SimpleGrantedAuthority("FLIGHT_CREW"));
                        break;
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
