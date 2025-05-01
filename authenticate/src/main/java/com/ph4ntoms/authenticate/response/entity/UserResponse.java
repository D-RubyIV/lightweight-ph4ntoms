package com.ph4ntoms.authenticate.response.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponse {
    private String firstName;
    private String username;
    private String password;
    private String lastName;
    private String phone;
    private String email;
    private LocalDate dob;
    private Boolean enable;
}
