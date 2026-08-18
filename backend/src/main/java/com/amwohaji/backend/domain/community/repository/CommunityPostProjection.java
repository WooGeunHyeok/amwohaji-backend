package com.amwohaji.backend.domain.community.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CommunityPostProjection {

    Long getPostId();
    String getAreaCd();
    String getSignguCd();
    String getTitle();
    String getContent();
    Integer getViewCount();
    LocalDateTime getInsDate();
    LocalDateTime getUpdDate();

    // JOIN으로 가져올 회원 정보
    String getNickname();
    String getProfileImage();
}
