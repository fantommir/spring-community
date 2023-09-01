package com.JKS.community.dto;

import com.JKS.community.entity.Post;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private int dislikeCount;

    // assuming Member and Category have fields 'id' and 'name'
    // if not, you may need to create separate DTOs for these entities
    private Long memberId;
    private String memberName;
    private Long categoryId;
    private String categoryName;

    public PostDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.viewCount = post.getViewCount();
        this.likeCount = post.getLikeCount();
        this.dislikeCount = post.getDislikeCount();

        // make sure that Member and Category are not null before accessing their fields
        if (post.getMember() != null) {
            this.memberId = post.getMember().getId();
            this.memberName = post.getMember().getName();  // assuming there is a getName() method in Member
        }

        if (post.getCategory() != null) {
            this.categoryId = post.getCategory().getId();
            this.categoryName = post.getCategory().getName();  // assuming there is a getName() method in Category
        }
    }
}
