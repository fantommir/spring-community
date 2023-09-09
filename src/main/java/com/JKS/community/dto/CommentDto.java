package com.JKS.community.dto;

import com.JKS.community.entity.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private Long id;
    private String content;
    private int likeCount;
    private int dislikeCount;
    private Long parentId;
    private int level;
    private boolean enabled;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    private Long memberId;
    private String memberName;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.likeCount = comment.getLikeCount();
        this.dislikeCount = comment.getDislikeCount();
        this.parentId = comment.getParentId();
        this.level = comment.getLevel();
        this.enabled = comment.isEnabled();
        this.createdDate = comment.getCreatedDate();
        this.modifiedDate = comment.getLastModifiedDate();

        if (comment.getMember() != null) {
            this.memberId = comment.getMember().getId();
            this.memberName = comment.getMember().getName();
        }

    }
}