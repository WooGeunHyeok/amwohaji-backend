package com.amwohaji.backend.global.file.service;

import com.amwohaji.backend.global.exception.CustomException;
import com.amwohaji.backend.global.exception.ErrorCode;
import com.amwohaji.backend.global.file.FileStorageProperties;
import com.amwohaji.backend.global.file.entity.Attachment;
import com.amwohaji.backend.global.file.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 앱 전역에서 재사용하는 파일 저장 유틸리티.
 * - referenceType은 이넘이 아닌 문자열이며, 연관 테이블명(예: "TBL_COMMUNITY")을 그대로 사용한다. {@link com.amwohaji.backend.global.file.entity.AttachmentReferenceType}의 상수를 사용할 것.
 * - 물리 파일은 file.upload.base-dir 하위에 {REFERENCE_TYPE}/{STORED_NAME}으로 저장한다.
 * - 저장/삭제와 함께 TBL_ATTACHMENT 레코드도 같이 관리한다.
 * - FILE_URL은 file.upload.base-url + 상대경로로 만들어지며, WebMvcConfig의 정적 리소스 핸들러("/files/**")가
 *   base-dir를 그대로 서빙하므로 실제로 접근 가능하다.
 *
 * 사용 예)
 * Attachment attachment = fileStorageService.store(file, AttachmentReferenceType.COMMUNITY, postId);
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final AttachmentRepository attachmentRepository;
    private final FileStorageProperties fileStorageProperties;

    // 허용할 이미지 및 동영상 확장자 목록 정의
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp", "bmp", "heic", "heif"
    );

    private static final Set<String> ALLOWED_VIDEO_EXTENSIONS = Set.of(
            "mp4", "avi", "mov", "wmv", "mkv", "webm", "flv"
    );

    /**
     * 파일 1개 저장
     */
    @Transactional
    public Attachment store(MultipartFile file, String referenceType, Long referenceId) {
        return store(file, referenceType, referenceId, fileStorageProperties.getMaxFileSize());
    }

    /**
     * 파일 1개 저장 (호출하는 곳에서 최대 용량 지정)
     */
    @Transactional
    public Attachment store(MultipartFile file, String referenceType, Long referenceId, long maxFileSize) {
        validate(file, maxFileSize);
        String safeReferenceType = sanitizeReferenceType(referenceType);

        String originalName = sanitize(file.getOriginalFilename());
        String extension = extractExtension(originalName);
        String storedName = createStoredName(extension);

        Path targetDir = Path.of(fileStorageProperties.getBaseDir(), safeReferenceType);
        Path targetPath = targetDir.resolve(storedName);

        try {
            Files.createDirectories(targetDir);
            file.transferTo(targetPath);
        } catch (IOException e) {
            log.error("파일 저장 실패 - referenceType: {}, referenceId: {}, fileName: {}",
                    referenceType, referenceId, originalName, e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        String fileUrl = fileStorageProperties.getBaseUrl() + "/" + safeReferenceType + "/" + storedName;

        Attachment attachment = Attachment.create(
                referenceType, referenceId, originalName, storedName, fileUrl, file.getSize(), extension);
        return attachmentRepository.save(attachment);
    }

    /**
     * 파일 여러 개 저장
     */
    @Transactional
    public List<Attachment> storeAll(List<MultipartFile> files, String referenceType, Long referenceId) {
        return storeAll(files, referenceType, referenceId, fileStorageProperties.getMaxFileSize());
    }

    /**
     * 파일 여러 개 저장 (호출하는 곳에서 파일 1개당 최대 용량 지정)
     */
    @Transactional
    public List<Attachment> storeAll(List<MultipartFile> files, String referenceType, Long referenceId, long maxFileSize) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        return files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> store(file, referenceType, referenceId, maxFileSize))
                .toList();
    }

    /**
     * 특정 도메인 데이터에 붙은 첨부파일 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Attachment> findByReference(String referenceType, Long referenceId) {
        return attachmentRepository.findByReferenceTypeAndReferenceIdOrderByAttachmentIdAsc(referenceType, referenceId);
    }

    /**
     * 첨부파일 단건 삭제 (물리 파일 + DB 레코드)
     */
    @Transactional
    public void delete(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ATTACHMENT_NOT_FOUND));

        deletePhysicalFile(attachment);
        attachmentRepository.delete(attachment);
    }

    /**
     * 특정 도메인 데이터에 붙은 첨부파일 전체 삭제 (예: 게시글 삭제 시 첨부파일 일괄 삭제)
     */
    @Transactional
    public void deleteAllByReference(String referenceType, Long referenceId) {
        List<Attachment> attachments = findByReference(referenceType, referenceId);
        attachments.forEach(this::deletePhysicalFile);
        attachmentRepository.deleteByReferenceTypeAndReferenceId(referenceType, referenceId);
    }

    private void deletePhysicalFile(Attachment attachment) {
        Path path = Path.of(fileStorageProperties.getBaseDir(), sanitizeReferenceType(attachment.getReferenceType()), attachment.getStoredName());
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("파일 삭제 실패 - attachmentId: {}, path: {}", attachment.getAttachmentId(), path, e);
            throw new CustomException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    /**
     * 특정 첨부파일 ID 목록만 선택 삭제 (게시물/댓글 첨부파일 수정 시 사용)
     */
    public void deleteAllByIds(List<Long> attachmentIds, String referenceType, Long referenceId) {
        // 보안을 위해 referenceType, referenceId에 속한 파일인지 함께 검증 조회
        List<Attachment> attachments = attachmentRepository.findAllByAttachmentIdInAndReferenceTypeAndReferenceId(
                attachmentIds, referenceType, referenceId
        );

        if (attachments.size() != attachmentIds.size()) {
            throw new CustomException(ErrorCode.INVALID_ATTACHMENT_DELETE_REQUEST);
        }

        // 1. 실제 물리 파일 삭제
        attachments.forEach(this::deletePhysicalFile);

        // 2. DB 레코드 삭제
        attachmentRepository.deleteAll(attachments);
    }

    private void validate(MultipartFile file) {
        validate(file, fileStorageProperties.getMaxFileSize());
    }

    private void validate(MultipartFile file, long maxFileSize) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_EMPTY);
        }
        if (maxFileSize > 0 && file.getSize() > maxFileSize) {
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        // 확장자 검증 로직 추가
        String originalName = sanitize(file.getOriginalFilename());
        String extension = extractExtension(originalName);

        if (!isAllowedExtension(extension)) {
            // 필요 시 ErrorCode에 UNSUPPORTED_FILE_TYPE 또는 INVALID_FILE_EXTENSION 추가 후 변경 권장
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private boolean isAllowedExtension(String extension) {
        if (extension == null || extension.isBlank()) {
            return false;
        }
        return ALLOWED_IMAGE_EXTENSIONS.contains(extension) || ALLOWED_VIDEO_EXTENSIONS.contains(extension);
    }

    private String sanitize(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "file";
        }
        // 경로 조작 방지: 디렉터리 구분자 제거 후 파일명만 남긴다.
        String name = Path.of(originalFilename).getFileName().toString();
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1).toLowerCase();
    }

    private String createStoredName(String extension) {
        String uuid = UUID.randomUUID().toString();
        if (extension == null || extension.isBlank()) {
            return uuid;
        }
        return uuid + "." + extension;
    }

    /**
     * referenceType이 물리 경로의 디렉터리명으로 그대로 쓰이므로, 값 자체가 경로 조작에 쓰이지 않도록 방어한다.
     * (영문/숫자/언더스코어만 허용)
     */
    private String sanitizeReferenceType(String referenceType) {
        if (referenceType == null || referenceType.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (!referenceType.matches("[A-Za-z0-9_]+")) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        return referenceType;
    }

    /**
     * STORED_NAME 그대로 노출하지 않고 첨부파일 목록을 응답 DTO로 변환하고 싶을 때 사용.
     */
    public Path resolvePath(Attachment attachment) {
        return Path.of(fileStorageProperties.getBaseDir(), sanitizeReferenceType(attachment.getReferenceType()), attachment.getStoredName());
    }
}
