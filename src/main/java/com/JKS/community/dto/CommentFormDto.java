package com.JKS.community.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CommentFormDto {
    @NotNull
    @Size(max = 1000)
    private String content;
    private Long parentId;
    private int level;
    private Long postId;
    private Long memberId;

    @Builder
    public CommentFormDto(String content, Long parentId, int level, Long postId, Long memberId) {
        this.content = content;
        this.parentId = parentId;
        this.level = level;
        this.postId = postId;
        this.memberId = memberId;
    }
}