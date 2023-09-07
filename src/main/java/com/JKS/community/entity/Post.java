package com.JKS.community.entity;

import com.JKS.community.dto.PostFormDto;
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
    private int viewCount = 0;
    private int likeCount = 0;
    private int dislikeCount = 0;

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
    private List<Reaction> reactions = new ArrayList<>();

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

    public void update(PostFormDto postFormDto) {
        this.title = postFormDto.getTitle();
        this.content = postFormDto.getContent();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }
}