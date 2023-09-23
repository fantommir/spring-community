package com.JKS.community.service;

import com.JKS.community.dto.MemberDto;
import com.JKS.community.dto.MemberFormDto;

import com.JKS.community.entity.Member;
import com.JKS.community.exception.InvalidPasswordException;
import com.JKS.community.exception.MemberAlreadyExistsException;
import com.JKS.community.exception.MemberNotFoundException;
import com.JKS.community.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class MemberServiceImplTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MemberFormDto memberFormDto;

    @BeforeEach
    void setUp() {
        Member member = Member.of("testEmail", "testName", "testPassword");

        memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("testEmail");
        memberFormDto.setPassword("testPassword");
        memberFormDto.setName("testName");
    }

    @Test
    void register() {
        MemberDto registeredMember = memberService.register(memberFormDto);

        assertThat(registeredMember.getEmail()).isEqualTo(memberFormDto.getEmail());
        assertThat(registeredMember.getName()).isEqualTo(memberFormDto.getName());
    }

    @Test
    void registerFailure() {
        memberService.register(memberFormDto);
        assertThatThrownBy(() -> memberService.register(memberFormDto))
                .isInstanceOf(MemberAlreadyExistsException.class);
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
        assertThat(retrievedMember.getEmail()).isEqualTo(registeredMember.getEmail());
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
        updatedMemberFormDto.setName(registeredMember.getName());
        updatedMemberFormDto.setPassword("updatedPassword");

        memberService.update(registeredMember.getId(), updatedMemberFormDto);

        Member updatedEntity = memberRepository.findById(registeredMember.getId())
                .orElseThrow(() -> new MemberNotFoundException("Invalid member Id:" + registeredMember.getId()));

        assertThat(updatedEntity.getId()).isEqualTo(registeredMember.getId());
        assertThat(updatedEntity.getEmail()).isEqualTo(registeredMember.getEmail());
        assertThat(updatedEntity.getName()).isEqualTo(updatedMemberFormDto.getName());

        assertThat(passwordEncoder.matches("updatedPassword", updatedEntity.getPassword())).isTrue();
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
