package com.JKS.community.dto;

import com.JKS.community.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class MemberDto {
    private Long id;
    private String email;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.createdDate = member.getCreatedDate();
        this.lastModifiedDate = member.getLastModifiedDate();
    }
}