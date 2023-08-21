//package com.JKS.community.entity;
//
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class Role {
//
//    @Id @GeneratedValue
//    @Column(name = "role_id")
//    private Long id;
//
//    @Enumerated(EnumType.STRING)
//    private Authority authority;
//
//    @OneToMany(mappedBy = "role")
//    private List<Member> members = new ArrayList<>();
//}
