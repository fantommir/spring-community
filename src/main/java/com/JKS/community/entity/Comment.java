package com.JKS.community.entity;

import com.JKS.community.entity.Base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    private String content;
    private int likeCount = 0;
    private int dislikeCount = 0;
    private String ref;
    private int level = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "comment")
    private List<Reaction> reactions = new ArrayList<>();

    public static Comment of(String ref, int level, Post post, Member member, String content) {
        Comment comment = new Comment();
        comment.ref = ref;
        comment.level = level;
        comment.post = post;
        comment.member = member;
        comment.content = content;

        post.addComment(comment);
        return comment;
    }
}
