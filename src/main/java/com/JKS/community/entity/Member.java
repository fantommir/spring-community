package com.JKS.community.entity;

import com.JKS.community.entity.Base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
public class Member extends BaseTimeEntity implements UserDetails {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false)
    private String password;

    private boolean enabled = true;


    @OneToMany(mappedBy = "member")
    private final List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private final List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private final List<Reaction> reactions = new ArrayList<>();

    public static Member of(String email, String name, String password) {
        Member member = new Member();
        member.email = email;
        member.password = password;
        member.name = name;
        return member;
    }

    public void update(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    // 계정 만료 여부 반환 ( ex. 일정 시간이 지나면 만료되는 평가판 사용자 )
    @Override
    public boolean isAccountNonExpired() {
        return true; // true : 만료 X
    }

    // 계정 잠금 여부 반환 ( ex. 로그인 시도 실패 횟수가 너무 많은 경우 )
    @Override
    public boolean isAccountNonLocked() {
        return true; // true : 잠금 X
    }

    // 패스워드 만료 여부 반환 ( ex. 일정 시간이 지나면 패스워드 변경 )
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // true : 만료 X
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
