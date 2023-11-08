package com.JKS.community.dto;

import com.JKS.community.entity.Comment;
import com.JKS.community.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
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
    private String createdDate;
    private String modifiedDate;
    private List<CommentDto> comments = new ArrayList<>();

    private Long parentCategoryId;
    private String parentCategoryName;

    public PostDto(Post post) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

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
        this.createdDate = (post.getCreatedDate() != null) ? post.getCreatedDate().format(formatter) : null;
        this.modifiedDate = (post.getLastModifiedDate() != null) ? post.getLastModifiedDate().format(formatter) : null;

        this.parentCategoryId = post.getCategory().getParent() != null ? post.getCategory().getParent().getId() : null;
        this.parentCategoryName = post.getCategory().getParent() != null ? post.getCategory().getParent().getName() : null;

        if (post.getComments() != null) {
            for (Comment comment : post.getComments()) {
                this.comments.add(new CommentDto(comment));
            }
        }
    }
}