package com.JKS.community.service;

import com.JKS.community.dto.MemberDto;
import com.JKS.community.dto.MemberFormDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {
    MemberDto register(MemberFormDto memberFormDto);

    // 회원 검색
    Page<MemberDto> getListByName(String name, Pageable pageable);

    // 회원 목록 조회
    Page<MemberDto> getList(Pageable pageable);

    // 회원 상세 정보 조회
    MemberDto get(Long memberId);

    // 회원 정보 수정
    MemberDto update(Long memberId, MemberFormDto memberFormDto);

    // 회원 탈퇴
    void withdrawal(Long memberId);

}