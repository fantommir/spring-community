package com.JKS.community.dto;

import com.JKS.community.entity.Comment;
import com.JKS.community.entity.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostDto {

    private Long id;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private int dislikeCount;
    private Boolean enabled;
    private Long memberId;
    private String memberName;
    private Long categoryId;
    private String categoryName;
    private List<CommentDto> comments = new ArrayList<>();

    public PostDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.viewCount = post.getViewCount();
        this.likeCount = post.getLikeCount();
        this.dislikeCount = post.getDislikeCount();
        this.enabled = post.getEnabled();
        this.memberId = post.getMember().getId();
        this.memberName = post.getMember().getName();
        this.categoryId = post.getCategory().getId();
        this.categoryName = post.getCategory().getName();

        if (post.getComments() != null) {
            for (Comment comment : post.getComments()) {
                this.comments.add(new CommentDto(comment));
            }
        }
    }
}