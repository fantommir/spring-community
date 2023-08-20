package com.JKS.community.entity;

import com.JKS.community.entity.Base.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Comment extends BaseEntity {

    @Id @GeneratedValue
    private Long id;

    private String content;
    private int like;
    private int dislike;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
