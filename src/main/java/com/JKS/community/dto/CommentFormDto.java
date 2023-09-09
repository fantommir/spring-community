package com.JKS.community.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CommentFormDto {

    private Long id;
    private String content;
    private Long parentId;
    private int level;
    private Long postId;
    private Long memberId;

    @Builder
    public CommentFormDto(Long id, String content, Long parentId, int level, Long postId, Long memberId) {
        this.id = id;
        this.content = content;
        this.parentId = parentId;
        this.level = level;
        this.postId = postId;
        this.memberId = memberId;
    }
}