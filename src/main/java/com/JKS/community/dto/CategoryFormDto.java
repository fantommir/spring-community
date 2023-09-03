package com.JKS.community.dto;

import com.JKS.community.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CategoryFormDto {

    private Long id;
    private String name;
    private Boolean enabled;
    private Long parentId;

    @Builder
    public CategoryFormDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.enabled = category.getEnabled();
        this.parentId = category.getParent() != null ? category.getParent().getId() : null;
    }
}