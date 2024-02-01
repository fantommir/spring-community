package com.JKS.community.dto;

import com.JKS.community.entity.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String email;
    private String name;
    private String createdDate;
    private String lastModifiedDate;

    @QueryProjection
    public MemberDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public MemberDto(Member member) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.createdDate = member.getCreatedDate().format(formatter);
        this.lastModifiedDate = member.getLastModifiedDate().format(formatter);
    }
}