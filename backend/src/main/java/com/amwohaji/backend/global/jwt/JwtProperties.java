package com.amwohaji.backend.global.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private long accessTokenExpiry;   // ms 단위 (예: 1800000 = 30분)
    private long refreshTokenExpiry;  // ms 단위 (예: 1209600000 = 14일)
}
