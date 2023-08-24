package com.JKS.community.repository;

import com.JKS.community.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllByNameContaining(String name);

    Member findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

}
