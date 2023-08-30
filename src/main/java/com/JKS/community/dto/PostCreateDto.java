package com.JKS.community.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateDto {
    private String title;
    private String content;
    private Long memberId; // Member ID
    private Long categoryId; // Category ID

    @Builder
    public PostCreateDto(String title, String content, Long memberId, Long categoryId) {
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.categoryId = categoryId;
    }
}