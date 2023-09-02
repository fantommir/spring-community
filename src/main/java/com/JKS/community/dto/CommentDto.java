package com.JKS.community.dto;

import com.JKS.community.entity.Comment;
import lombok.Getter;
import lombok.Setter;

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

    // assuming Member has fields 'id' and 'name'
    // if not, you may need to create separate DTOs for these entities
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

        // make sure that Member is not null before accessing its fields
        if (comment.getMember() != null) {
            this.memberId = comment.getMember().getId();
            // assuming there is a getName() method in Member
            // replace with appropriate method call if needed
            this.memberName = comment.getMember().getName();
        }

    }
}