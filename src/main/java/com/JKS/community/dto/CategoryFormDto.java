package com.JKS.community.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CategoryFormDto {

    private String name;
    private Boolean enabled;
    private Long parentId;

    @Builder
    public CategoryFormDto(String name, Boolean enabled, Long parentId) {
        this.name = name;
        this.enabled = enabled;
        this.parentId = parentId;
    }
}