package com.amwohaji.backend.global.like.repository;

import com.amwohaji.backend.global.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserIdAndReferenceTypeAndReferenceId(Long userId, String referenceType, Long referenceId);

    void deleteByUserIdAndReferenceTypeAndReferenceId(Long userId, String referenceType, Long referenceId);
}
