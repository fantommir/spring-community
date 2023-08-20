package com.JKS.community.entity;

import com.JKS.community.entity.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Post extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;
    private String content;
    private int view_count;
    private int like;
    private int dislike;

    private Boolean is_deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // comment
    @OneToMany(mappedBy = "post")
    private List<Comment> comment = new ArrayList<>();
}
