package com.amwohaji.backend.global.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SecurityContext에 저장되는 인증 주체.
 * Controller에서 @AuthenticationPrincipal UserPrincipal principal 로 꺼낼 수 있다.
 */
@Getter
@AllArgsConstructor
public class UserPrincipal {

    private final Long userId;
    private final String role;
}
