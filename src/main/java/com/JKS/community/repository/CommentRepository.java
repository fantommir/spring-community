package com.JKS.community.repository;

import com.JKS.community.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPostIdAndEnabledTrue(Long postId, Pageable pageable);

    Page<Comment> findAllByMemberIdAndEnabledTrue(Long memberId);
}
