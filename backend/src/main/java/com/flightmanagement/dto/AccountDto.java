package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private Integer accountId;
    private String accountName;
    private String password;
    private String email;
    private String citizenId;
    private String phoneNumber;
    private Integer accountType;
    private String accountTypeName;
    private String role;

}
