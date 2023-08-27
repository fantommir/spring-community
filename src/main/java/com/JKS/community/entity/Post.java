package com.JKS.community.entity;

import com.JKS.community.dto.PostCreateDto;
import com.JKS.community.dto.PostUpdateDto;
import com.JKS.community.entity.Base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;
    private String content;
    private int view_count = 0;
    private int like_count = 0;
    private int dislike_count = 0;

    private Boolean enabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // comment
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Reaction> reaction;

    @Builder
    public Post(String title, String content, Member member, Category category) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.category = category;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void update(PostUpdateDto postUpdateDto) {
        this.title = postUpdateDto.getTitle();
        this.content = postUpdateDto.getContent();
    }

    public void increaseViewCount() {
        this.view_count++;
    }

    public void increaseLikeCount() {
        this.like_count++;
    }

    public void increaseDislikeCount() {
        this.dislike_count++;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }
}
