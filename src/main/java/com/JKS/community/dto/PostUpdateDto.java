package com.JKS.community.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateDto {
    private String title;
    private String content;

    public PostUpdateDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
