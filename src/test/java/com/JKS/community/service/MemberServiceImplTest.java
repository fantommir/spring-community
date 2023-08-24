package com.JKS.community.service;

import com.JKS.community.entity.Member;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberServiceImplTest {

    @Autowired
    MemberService memberService;

    @Test
    public void successful_register() throws Exception {
        // given
        Member member0 = createMember(0);
        Member member1 = createMember(1);
        Member member2 = createMember(2);

        // when
        memberService.registerMember(member0);
        memberService.registerMember(member1);
        memberService.registerMember(member2);

        // then
    }

    private Member createMember(int num) {
        return Member.builder()
                .loginId("test" + num)
                .password("test" + num)
                .name("test" + num)
                .build();
    }

    @Test
    public void failed_register() throws Exception {
        // given

        // when

        // then
    }
}