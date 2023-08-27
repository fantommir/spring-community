package com.JKS.community.service;

import com.JKS.community.entity.Member;
import com.JKS.community.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class PostServiceImplTest {

    @Autowired
    private PostService postService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PostRepository postRepository;

    @AfterEach
    void afterEach() {
        postRepository.deleteAll();
    }

    @Test
    void successful_create_post() {
        Member member = Member.builder()
                        .loginId("test")
                        .password("test")
                        .name("test").build();
        memberService.registerMember(member);
    }


}