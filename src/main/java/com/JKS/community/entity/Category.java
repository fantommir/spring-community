package com.JKS.community.entity;

import com.JKS.community.dto.CategoryFormDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE category SET enabled = false WHERE category_id = ?")
@Where(clause = "enabled = true")
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;
    private Boolean enabled = true;

    private int depth = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Getter
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder
    public static Category of(String name, Category parent, Boolean enabled) {
        Category category = new Category();
        category.name = name;
        category.parent = parent;
        category.depth = parent != null ? parent.getDepth() + 1 : 0;
        category.enabled = enabled;
        if (parent != null) {
            parent.addChildCategory(category);
        }
        return category;
    }

    private void addChildCategory(Category category) {
        this.children.add(category);
    }

    public void update(CategoryFormDto categoryFormDto, Category parent) {
        this.name = categoryFormDto.getName();
        this.enabled = categoryFormDto.getEnabled();
        if (parent != null) {
            if (this.parent != null) {
                this.parent.getChildren().remove(this); // 기존 부모 카테고리에서 제거
            }
            parent.addChildCategory(this); // 새 부모 카테고리에 추가
            this.parent = parent;
            this.depth = parent.getDepth() + 1;
        } else {
            // parentId가 null인 경우 (즉, 최상위 카테고리인 경우)
            if (this.parent != null) {
                this.parent.getChildren().remove(this); // 기존 부모 카테고리에서 제거
                this.depth = 0; // depth 재설정
                this.parent = null;
            }
        }
    }

}
