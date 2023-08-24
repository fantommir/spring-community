package com.JKS.community.service;

import com.JKS.community.entity.Member;
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
class MemberServiceImplTest {

    @AfterEach
    public void afterEach() {
        memberService.getMemberList().forEach(member -> memberService.withdrawalMember(member.getId()));
    }

    @Autowired
    MemberService memberService;

    @Test
    public void successful_register() throws Exception {
        // given
        Member member0 = createMember(0);
        Member member1 = createMember(1);
        Member member2 = createMember(2);

        // then
        assertEquals(member0, memberService.getMember(member0.getId()));
        assertEquals(member1, memberService.getMember(member1.getId()));
        assertEquals(member2, memberService.getMember(member2.getId()));
        assertEquals(3, memberService.getMemberList().size());

    }

    private Member createMember(int num) {
        Member member = Member.builder()
                .loginId("test" + num)
                .password("test" + num)
                .name("test" + num)
                .build();

        memberService.registerMember(member);
        return member;
    }

    @Test
    public void failed_register() throws Exception {
        // given
        createMember(0);
        Member sameLoginIdMember = Member.builder()
                .loginId("test0")
                .password("sameLoginId")
                .name("sameLoginId")
                .build();

        // when & then
        assertThrows(IllegalArgumentException.class, () -> memberService.registerMember(sameLoginIdMember));
    }

    @Test
    public void successful_login() throws Exception {
        // given
        Member member = createMember(0);

        // when
        Member loginMember = memberService.login(member.getLoginId(), member.getPassword());

        // then
        assertEquals(member, loginMember);
    }

    @Test
    public void failed_login_by_loginId() throws Exception {
        // given
        createMember(0);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> memberService.login("notExistLoginId", "test0"));
    }

    @Test
    public void failed_login_by_password() throws Exception {
        // given
        Member member = createMember(0);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> memberService.login(member.getLoginId(), "notExistPassword"));
    }



    @Test
    public void successful_searchMembersByName() throws Exception {
        // given
        createMember(0);
        createMember(1);
        createMember(2);

        // when
        String name = "test";
        assertEquals(3, memberService.searchMembersByName(name).size());
    }

    @Test
    public void successful_getMemberList() throws Exception {
        // given
        createMember(0);
        createMember(1);
        createMember(2);

        // when
        assertEquals(3, memberService.getMemberList().size());
    }

    @Test
    public void successful_getMember() throws Exception {
        // given
        Member member = createMember(0);

        // when
        assertEquals(member, memberService.getMember(member.getId()));
    }

    @Test
    public void failed_getMember() throws Exception {
        // given
        createMember(0);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> memberService.getMember(100L));
    }

    @Test
    public void successful_updateMember() throws Exception {
        // given
        Member member = createMember(0);
        Member updatedPasswordMember = Member.builder()
                .password("updatedPassword")
                .name("sameName")
                .build();
        Member updatedNameMember = Member.builder()
                .password("samePassword")
                .name("updatedName")
                .build();

        // when
        Member updatedMember = memberService.updateMember(member.getId(), updatedPasswordMember);

        // then
        assertEquals(updatedPasswordMember.getPassword(), updatedMember.getPassword());
        assertEquals(updatedPasswordMember.getName(), updatedMember.getName());
    }

    @Test
    public void failed_updateMember() throws Exception {
        // given
        Member member = createMember(0);
        Member updatedMember = Member.builder()
                .password("updatedPassword")
                .name("sameName")
                .build();

        // when & then
        assertThrows(IllegalArgumentException.class, () -> memberService.updateMember(100L, updatedMember));
    }

    @Test
    public void successful_withdrawalMember() throws Exception {
        // given
        Member member = createMember(0);

        // when
        memberService.withdrawalMember(member.getId());

        // then
        assertEquals(0, memberService.getMemberList().size());
    }

    @Test
    public void failed_withdrawalMember() throws Exception {
        // given
        createMember(0);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> memberService.withdrawalMember(100L));
    }


}