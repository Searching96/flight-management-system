package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private Integer accountId;
    private String accountName;
    private String email;
    private Integer accountType;
    private String token;
    
    public LoginResponseDto(Integer accountId, String accountName, String email, Integer accountType) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.email = email;
        this.accountType = accountType;
    }
}
