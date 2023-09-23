package com.JKS.community.dto;

import com.JKS.community.entity.Comment;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@ToString
public class CommentDto {
    private Long id;
    private String content;
    private int likeCount;
    private int dislikeCount;
    private Long parentId;
    private List<CommentDto> children = new ArrayList<>();
    private int level;
    private boolean enabled;
    private String createdDate;
    private String modifiedDate;

    private Long memberId;
    private String memberName;

    public CommentDto(Comment comment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

        this.id = comment.getId();
        this.content = comment.getContent();
        this.likeCount = comment.getLikeCount();
        this.dislikeCount = comment.getDislikeCount();
        this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
        this.level = comment.getLevel();
        this.enabled = comment.isEnabled();
        this.createdDate = comment.getCreatedDate().format(formatter);
        this.modifiedDate = comment.getLastModifiedDate().format(formatter);

        if (comment.getMember() != null) {
            this.memberId = comment.getMember().getId();
            this.memberName = comment.getMember().getName();
        }

        if (comment.getChildren() != null) {
            for (Comment child : comment.getChildren()) {
                this.children.add(new CommentDto(child));
            }
        }
    }
}
