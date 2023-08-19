package com.JKS.community.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Role {

    @Id @GeneratedValue
    @Column(name = "role_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @OneToMany(mappedBy = "role")
    private List<User> user = new ArrayList<>();
}
