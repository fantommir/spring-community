package com.JKS.community.service;

import com.JKS.community.dto.MemberDto;
import com.JKS.community.dto.MemberFormDto;

import com.JKS.community.entity.Member;
import com.JKS.community.exception.InvalidPasswordException;
import com.JKS.community.exception.MemberAlreadyExistsException;
import com.JKS.community.exception.MemberNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class MemberServiceImplTest {

    @Autowired
    private MemberService memberService;

    private MemberFormDto memberFormDto;
    private MemberDto memberDto;
    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .loginId("testLoginId")
                .password("testPassword")
                .name("testName")
                .build();

        memberFormDto = new MemberFormDto();
        memberFormDto.setLoginId("testLoginId");
        memberFormDto.setPassword("testPassword");
        memberFormDto.setName("testName");

        memberDto = new MemberDto(member);
    }

    @Test
    void register() {
        MemberDto registeredMember = memberService.register(memberFormDto);

        assertThat(registeredMember.getLoginId()).isEqualTo(memberFormDto.getLoginId());
        assertThat(registeredMember.getName()).isEqualTo(memberFormDto.getName());
    }

    @Test
    void registerFailure() {
        memberService.register(memberFormDto);
        assertThatThrownBy(() -> memberService.register(memberFormDto))
                .isInstanceOf(MemberAlreadyExistsException.class);
    }

    @Test
    void login() {
        memberService.register(memberFormDto);
        MemberDto loggedInMember = memberService.login(memberFormDto);

        assertThat(loggedInMember.getLoginId()).isEqualTo(memberFormDto.getLoginId());
        assertThat(loggedInMember.getName()).isEqualTo(memberFormDto.getName());
    }

    @Test
    void loginFailureInvalidLoginId() {
        MemberFormDto invalidLoginIdMemberFormDto = new MemberFormDto();
        invalidLoginIdMemberFormDto.setLoginId("invalidLoginId");
        invalidLoginIdMemberFormDto.setPassword("testPassword");

        assertThatThrownBy(() -> memberService.login(invalidLoginIdMemberFormDto))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void loginFailureInvalidPassword() {
        memberService.register(memberFormDto);

        MemberFormDto invalidPasswordMemberFormDto = new MemberFormDto();
        invalidPasswordMemberFormDto.setLoginId("testLoginId");
        invalidPasswordMemberFormDto.setPassword("invalidPassword");

        assertThatThrownBy(() -> memberService.login(invalidPasswordMemberFormDto))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    void getListByName() {
        memberService.register(memberFormDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<MemberDto> memberPage = memberService.getListByName(memberFormDto.getName(), pageable);

        assertThat(memberPage.getContent()).hasSize(1);
        assertThat(memberPage.getContent().get(0).getName()).isEqualTo(memberFormDto.getName());
    }

    @Test
    void getListByNameFailure() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MemberDto> memberPage = memberService.getListByName("nonExistingName", pageable);

        assertThat(memberPage.getContent()).isEmpty();
    }

    @Test
    void getList() {
        memberService.register(memberFormDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<MemberDto> memberPage = memberService.getList(pageable);

        assertThat(memberPage.getContent()).hasSize(1);
        assertThat(memberPage.getContent().get(0).getName()).isEqualTo(memberFormDto.getName());
    }

    @Test
    void getListFailure() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MemberDto> memberPage = memberService.getList(pageable);

        assertThat(memberPage.getContent()).isEmpty();
    }


    @Test
    void get() {
        MemberDto registeredMember = memberService.register(memberFormDto);
        MemberDto retrievedMember = memberService.get(registeredMember.getId());

        assertThat(retrievedMember.getId()).isEqualTo(registeredMember.getId());
        assertThat(retrievedMember.getLoginId()).isEqualTo(registeredMember.getLoginId());
        assertThat(retrievedMember.getName()).isEqualTo(registeredMember.getName());
    }

    @Test
    void getFailure() {
        assertThatThrownBy(() -> memberService.get(999L))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void update() {
        MemberDto registeredMember = memberService.register(memberFormDto);

        MemberFormDto updatedMemberFormDto = new MemberFormDto();
        updatedMemberFormDto.setName("updatedName");
        MemberDto updatedMember = memberService.update(registeredMember.getId(), updatedMemberFormDto);

        assertThat(updatedMember.getId()).isEqualTo(registeredMember.getId());
        assertThat(updatedMember.getLoginId()).isEqualTo(registeredMember.getLoginId());
        assertThat(updatedMember.getName()).isEqualTo(updatedMemberFormDto.getName());
    }

    @Test
    void updateFailure() {
        assertThatThrownBy(() -> memberService.update(999L, new MemberFormDto()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void withdrawal() {
        MemberDto registeredMember = memberService.register(memberFormDto);
        memberService.withdrawal(registeredMember.getId());

        assertThatThrownBy(() -> memberService.get(registeredMember.getId()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void withdrawalFailure() {
        assertThatThrownBy(() -> memberService.withdrawal(999L))
                .isInstanceOf(MemberNotFoundException.class);
    }


}
