package com.amwohaji.backend.global.like.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 앱 공용 좋아요 관리 테이블 (TBL_LIKE).
 * REFERENCE_TYPE + REFERENCE_ID 조합으로 어떤 도메인의 어떤 대상에 대한 좋아요인지 식별한다.
 */
@Getter
@Entity
@Table(name = "TBL_LIKE",
        uniqueConstraints = @UniqueConstraint(name = "UQ_LIKE_USER_TARGET", columnNames = {"USER_ID", "REFERENCE_TYPE", "REFERENCE_ID"}),
        indexes = @Index(name = "IDX_LIKE_TARGET", columnList = "REFERENCE_TYPE, REFERENCE_ID"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIKE_ID")
    private Long likeId;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "REFERENCE_TYPE", length = 50, nullable = false)
    private String referenceType;

    @Column(name = "REFERENCE_ID", nullable = false)
    private Long referenceId;

    @Column(name = "INSDATE", nullable = false, updatable = false, insertable = false)
    private LocalDateTime insDate;

    private Like(Long userId, String referenceType, Long referenceId) {
        this.userId = userId;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
    }

    public static Like create(Long userId, String referenceType, Long referenceId) {
        return new Like(userId, referenceType, referenceId);
    }
}
