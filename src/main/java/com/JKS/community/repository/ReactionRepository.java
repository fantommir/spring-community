package com.JKS.community.repository;

import com.JKS.community.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Optional<Reaction> findByPostIdAndMemberId(Long postId, Long memberId);
}
