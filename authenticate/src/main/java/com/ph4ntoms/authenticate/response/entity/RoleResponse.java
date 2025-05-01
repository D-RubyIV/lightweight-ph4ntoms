package com.ph4ntoms.authenticate.response.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleResponse {
    private String name;
    private String code;
    private Boolean enable;
    private String description;
}
