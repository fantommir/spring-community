package com.JKS.community.repository;

import com.JKS.community.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Page<Member> findAllByNameContaining(String name, Pageable pageable);

    Optional<Member> findByLoginId(String loginId);

    Boolean existsByLoginId(String loginId);

}
