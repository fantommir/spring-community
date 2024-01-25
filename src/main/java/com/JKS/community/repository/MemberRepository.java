package com.JKS.community.repository;

import com.JKS.community.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Page<Member> findAllByNameContaining(String name, Pageable pageable);

    Optional<Member> findByEmail(String loginId);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.member = :member")
    long countPostsByMember(@Param("member") Member member);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.member = :member")
    long countCommentsByMember(@Param("member") Member member);
}
