package com.ph4ntoms.authenticate.response.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleResponse extends BaseResponse {
    private String name;
    private String code;
    private String description;
    private Set<PermissionResponse> permissions;
}
