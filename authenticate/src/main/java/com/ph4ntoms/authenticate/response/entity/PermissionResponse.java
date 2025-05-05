package com.ph4ntoms.authenticate.response.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PermissionResponse extends BaseResponse  {
    private String name;
    private String code;
    private String description;
}
