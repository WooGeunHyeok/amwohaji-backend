package com.amwohaji.backend.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "TBL_COMMUNITY_POST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID", nullable = false, unique = true)
    private Long postId;

    @Column(name = "USER_ID", length = 20, nullable = false)
    private Long userId;

    @Column(name = "AREACD", length = 10, nullable = false)
    private String areaCd;

    @Column(name = "SIGNGUCD", length = 10, nullable = false)
    private String signguCd;

    @Column(name = "TITLE", length = 255, nullable = false)
    private String title;

    @Column(name = "CONTENT", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "VIEW_COUNT", nullable = false)
    private Integer viewCount;

    @CreationTimestamp
    @Column(name = "INSDATE", nullable = false, updatable = false)
    private LocalDateTime insDate;

    @LastModifiedDate
    @Column(name = "UPDDATE", nullable = true)
    private LocalDateTime updDate;

    @Column(name = "IS_DELETED", length = 1, nullable = false)
    private String isDeleted;

    /**
     * 게시물 수정 메서드
     */
    public void updatePost(String areaCd, String signguCd, String title, String content ) {
        if (areaCd != null) this.areaCd = areaCd;
        if (signguCd != null) this.signguCd = signguCd;
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        this.updDate = LocalDateTime.now();
    }
}
