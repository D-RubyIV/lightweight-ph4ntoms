package com.ph4ntoms.authenticate.request.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActivateRequest {
    private String sign;
    private String code;
}
