package com.JKS.community.dto;

import com.JKS.community.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentFormDto {

    private Long id;
    private String content;
    private Long parentId;
    private int level;
    private Long postId;
    private Long memberId;

    @Builder
    public CommentFormDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.parentId = comment.getParentId();
        this.level = comment.getLevel();
        this.postId = comment.getPost().getId();
        this.memberId = comment.getMember().getId();
    }
}