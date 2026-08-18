package com.amwohaji.backend.global.file.repository;

import com.amwohaji.backend.global.file.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByReferenceTypeAndReferenceIdOrderByAttachmentIdAsc(
            String referenceType, Long referenceId);

    void deleteByReferenceTypeAndReferenceId(String referenceType, Long referenceId);

    List<Attachment> findAllByAttachmentIdInAndReferenceTypeAndReferenceId(
            List<Long> attachmentIds, String referenceType, Long referenceId);
}
