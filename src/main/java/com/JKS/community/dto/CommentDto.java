package com.JKS.community.dto;

import com.JKS.community.entity.Comment;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;

@Getter @Setter
@ToString
public class CommentDto {
    private Long id;
    private String content;
    private int likeCount;
    private int dislikeCount;
    private Long parentId;
    private int level;
    private boolean enabled;
    private String createdDate;
    private String modifiedDate;

    private Long memberId;
    private String memberName;
    private int childrenSize;
    private String parentName;

    public CommentDto(Comment comment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

        this.id = comment.getId();
        this.content = comment.getContent();
        this.likeCount = comment.getLikeCount();
        this.dislikeCount = comment.getDislikeCount();
        this.parentId = comment.getParent() == null ? null : comment.getParent().getId();
        this.level = comment.getLevel();
        this.enabled = comment.isEnabled();
        this.createdDate = comment.getCreatedDate().format(formatter);
        this.modifiedDate = comment.getLastModifiedDate().format(formatter);

        this.memberId = comment.getMember().getId();
        this.memberName = comment.getMember().getName();
        this.childrenSize = comment.getChildren().size();
        this.parentName = comment.getParent() == null ? null : comment.getParent().getMember().getName();
    }
}
