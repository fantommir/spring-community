package com.JKS.community.repository;

import com.JKS.community.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllByNameContaining(String name);

    Optional<Member> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

}
