package com.JKS.community.dto;

import com.JKS.community.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberDto {
    private Long id;
    private String loginId;
    private String name;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.loginId = member.getLoginId();
        this.name = member.getName();
    }
}