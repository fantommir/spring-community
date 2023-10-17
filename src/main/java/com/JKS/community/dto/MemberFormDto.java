package com.JKS.community.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MemberFormDto {

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 2, max = 10)
    private String name;

    private String password;

    private String confirm_password;

    @Builder
    public MemberFormDto(String email, String password, String confirm_password, String name) {
        this.email = email;
        this.password = password;
        this.confirm_password = confirm_password;
        this.name = name;
    }
}
