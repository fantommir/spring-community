package com.JKS.community.dto;

import com.JKS.community.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class PostFormDto {

    private Long id;
    private String title;
    private String content;
    private Long memberId;
    private Long categoryId;

    @Builder
    public PostFormDto(Long id, String title, String content, Long memberId, Long categoryId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.categoryId = categoryId;
    }
}