package com.JKS.community.service;

import com.JKS.community.entity.Member;

import java.util.List;

public interface MemberService {
    void registerMember(Member member);
    Member login(String username, String password);

    // 회원 검색
    List<Member> searchMembersByName(String name);

    // 회원 목록 조회
    List<Member> getMemberList();

    // 회원 상세 정보 조회
    Member getMember(Long memberId);

    // 회원 정보 수정
    Member updateMember(Long memberId, Member updatedMember);

    // 회원 탈퇴
    void withdrawalMember(Long memberId);
}