package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto {
    private Integer id;
    private String accountName;
    private String email;
    private String role;
    private String accountTypeName;
}
