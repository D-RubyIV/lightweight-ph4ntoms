package com.ph4ntoms.authenticate.response.entity;

import com.ph4ntoms.authenticate.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupResponse extends BaseResponse {
    private String name;
    private String code;
    private String description;
    private Set<Role> roles;
}
