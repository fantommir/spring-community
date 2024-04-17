package com.JKS.community.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Reaction {

    @Id @GeneratedValue
    @Column(name = "reaction_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Enumerated(EnumType.STRING)
    private ReactionType reactionType;

    // Post에 대한 Reaction 생성
    public static Reaction of(Member member, Post post, Boolean isLike) {
        Reaction reaction = new Reaction();
        reaction.member = member;
        reaction.post = post;
        reaction.reactionType = isLike ? ReactionType.LIKE : ReactionType.DISLIKE;
        return reaction;
    }

    // Comment에 대한 Reaction 생성
    public static Reaction of(Member member, Comment comment, Boolean isLike) {
        Reaction reaction = new Reaction();
        reaction.member = member;
        reaction.comment = comment;
        reaction.reactionType = isLike ? ReactionType.LIKE : ReactionType.DISLIKE;
        return reaction;
    }

    public void update(Boolean isLike) {
        this.reactionType = isLike ? ReactionType.LIKE : ReactionType.DISLIKE;
    }

    public boolean isLike() {
        return this.reactionType == ReactionType.LIKE;
    }

}
