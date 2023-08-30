package com.JKS.community.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDto {
    private Long postId;
    private Long memberId;
    private Long parentId;
    private String content;

    @Builder
    public CommentCreateDto(Long postId, Long memberId, Long parentId, String content) {
        this.postId = postId;
        this.memberId = memberId;
        this.parentId = parentId;
        this.content = content;
    }
}
