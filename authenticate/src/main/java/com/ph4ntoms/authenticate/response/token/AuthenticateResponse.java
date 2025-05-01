package com.ph4ntoms.authenticate.response.token;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AuthenticateResponse {
    private String accessToken;
    private String refreshToken;
}
