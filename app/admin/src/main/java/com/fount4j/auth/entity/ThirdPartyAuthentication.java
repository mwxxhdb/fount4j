package com.fount4j.auth.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdPartyAuthentication {
    private String accessToken;
    private int expireIn;
    private String refreshToken;
    private int refreshTokenExpireIn;
    private String uid;
    private String openId;
    private String accessCode;
    private String unionId;
}
