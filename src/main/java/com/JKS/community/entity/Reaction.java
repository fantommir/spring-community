package com.JKS.community.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Reaction {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private ReactionType reactionType;

    public static Reaction of(Member member, Post post, boolean isLike) {
        Reaction reaction = new Reaction();
        reaction.member = member;
        reaction.post = post;
        reaction.reactionType = isLike ? ReactionType.LIKE : ReactionType.DISLIKE;
        return reaction;
    }


    public void toggleReaction() {
        if (this.reactionType == ReactionType.LIKE) {
            this.reactionType = ReactionType.DISLIKE;
        } else {
            this.reactionType = ReactionType.LIKE;
        }
    }

}
