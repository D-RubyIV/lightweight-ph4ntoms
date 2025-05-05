package com.ph4ntoms.authenticate.request.entity;

import com.ph4ntoms.authenticate.group.CreateMethod;
import com.ph4ntoms.authenticate.group.UpdateMethod;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class UserRequest {
    @NotBlank(message = "First name is required", groups = {CreateMethod.class, UpdateMethod.class})
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters", groups = {CreateMethod.class, UpdateMethod.class})
    private String firstName;

    @NotBlank(message = "Last name is required", groups = {CreateMethod.class, UpdateMethod.class})
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters", groups = {CreateMethod.class, UpdateMethod.class})
    private String lastName;

    @NotBlank(message = "Username is required", groups = {CreateMethod.class, UpdateMethod.class})
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters", groups = {CreateMethod.class, UpdateMethod.class})
    private String username;

    @NotBlank(message = "Password is required", groups = {CreateMethod.class})
    @Size(min = 6, message = "Password must be at least 6 characters", groups = {CreateMethod.class})
    private String password;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format", groups = {CreateMethod.class, UpdateMethod.class})
    private String phone;

    @NotBlank(message = "Email is required", groups = {CreateMethod.class, UpdateMethod.class})
    @Email(message = "Invalid email format", groups = {CreateMethod.class, UpdateMethod.class})
    private String email;

    private LocalDate dob;
    private Boolean enabled = true;
    private List<UUID> groupIds;
} 