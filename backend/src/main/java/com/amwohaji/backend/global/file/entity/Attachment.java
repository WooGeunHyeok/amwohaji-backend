package com.amwohaji.backend.global.file.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 앱 공용 첨부파일 관리 테이블 (TBL_ATTACHMENT).
 * REFERENCE_TYPE + REFERENCE_ID 조합으로 어떤 도메인의 어떤 글/데이터에 붙은 파일인지 식별한다.
 */
@Getter
@Entity
@Table(name = "TBL_ATTACHMENT",
        indexes = @Index(name = "IDX_ATTACHMENT_REF", columnList = "REFERENCE_TYPE, REFERENCE_ID"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTACHMENT_ID")
    private Long attachmentId;

    @Column(name = "REFERENCE_TYPE", length = 50, nullable = false)
    private String referenceType;

    @Column(name = "REFERENCE_ID", nullable = false)
    private Long referenceId;

    @Column(name = "ORIGINAL_NAME", length = 255, nullable = false)
    private String originalName;

    @Column(name = "STORED_NAME", length = 255, nullable = false)
    private String storedName;

    @Lob
    @Column(name = "FILE_URL", columnDefinition = "TEXT", nullable = false)
    private String fileUrl;

    @Column(name = "FILE_SIZE", nullable = false)
    private Long fileSize;

    @Column(name = "FILE_EXTENSION", length = 10, nullable = false)
    private String fileExtension;

    @Column(name = "INSDATE", nullable = false, updatable = false, insertable = false)
    private LocalDateTime insDate;

    private Attachment(String referenceType, Long referenceId, String originalName,
                        String storedName, String fileUrl, Long fileSize, String fileExtension) {
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.originalName = originalName;
        this.storedName = storedName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.fileExtension = fileExtension;
    }

    public static Attachment create(String referenceType, Long referenceId, String originalName,
                                     String storedName, String fileUrl, Long fileSize, String fileExtension) {
        return new Attachment(referenceType, referenceId, originalName, storedName, fileUrl, fileSize, fileExtension);
    }
}
