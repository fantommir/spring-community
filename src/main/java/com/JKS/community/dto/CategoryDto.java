package com.JKS.community.dto;

import com.JKS.community.entity.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class CategoryDto {

    private Long id;
    private String name;
    private Boolean enabled;
    private int depth;
    private Long parentId;
    private List<CategoryDto> children = new ArrayList<>();

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.enabled = category.getEnabled();
        this.depth = category.getDepth();
        this.parentId = category.getParent() != null ? category.getParent().getId() : null;

        if (category.getChildren() != null) {
            for (Category child : category.getChildren()) {
                this.children.add(new CategoryDto(child));
            }
        }
    }
}
