package com.JKS.community.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class PostFormDto {

    @NotNull
    @Size(max = 50)
    private String title;

    @NotNull
    @Size(max = 65535)
    private String content;

    @NotNull
    private Long memberId;

    @NotNull
    private Long categoryId;


    @Builder
    public PostFormDto(String title, String content, Long memberId, Long categoryId) {
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.categoryId = categoryId;
    }
}