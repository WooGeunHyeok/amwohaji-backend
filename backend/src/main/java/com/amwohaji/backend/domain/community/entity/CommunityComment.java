package com.amwohaji.backend.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "TBL_COMMUNITY_COMMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommunityComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID", nullable = false, unique = true)
    private Long commentId;

    @Column(name = "USER_ID", length = 20, nullable = false)
    private Long userId;

    @Column(name = "POST_ID", length = 20, nullable = false)
    private Long postId;

    @Column(name = "PARENT_ID", length = 20)
    private Long parentId;

    @Column(name = "CONTENT", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "IS_DELETED", length = 1, nullable = false)
    private String isDeleted;

    @CreationTimestamp
    @Column(name = "INSDATE", nullable = false, updatable = false)
    private LocalDateTime insDate;

    @LastModifiedDate
    @Column(name = "UPDDATE", nullable = true)
    private LocalDateTime updDate;

    public void updateComment(String comment) {
        if (comment != null) this.content = comment;
        this.updDate = LocalDateTime.now();
    }
}
