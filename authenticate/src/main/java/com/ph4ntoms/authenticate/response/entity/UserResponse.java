package com.ph4ntoms.authenticate.response.entity;

import com.ph4ntoms.authenticate.model.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.mapstruct.AfterMapping;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponse extends BaseResponse  {
    private String firstName;
    private String username;
    private String lastName;
    private String phone;
    private String email;
    private Long balance;
    private LocalDate dob;
    private Set<Group> groups;
}
