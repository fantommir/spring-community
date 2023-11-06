package com.JKS.community.service;

import com.JKS.community.dto.MemberDto;
import com.JKS.community.dto.MemberFormDto;
import com.JKS.community.entity.Member;
import com.JKS.community.exception.member.*;
import com.JKS.community.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberServiceImpl memberService;

    private MemberFormDto memberFormDto;
    private Member member;
    private final Long memberId = 1L;

    @BeforeEach
    void setUp() {
        memberFormDto = MemberFormDto.builder()
                .name("name")
                .email("email")
                .password("password")
                .confirm_password("password")
                .build();

        member = Member.of(memberFormDto.getEmail(), memberFormDto.getName(), memberFormDto.getPassword());
    }

    @Test
    @DisplayName("회원가입-성공")
    public void register() {
        // given

        when(memberRepository.findByEmail(memberFormDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(memberFormDto.getPassword())).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // when
        MemberDto result = memberService.register(memberFormDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(memberFormDto.getEmail());
        assertThat(result.getName()).isEqualTo(memberFormDto.getName());

        verify(memberRepository, times(1)).findByEmail(memberFormDto.getEmail());
        verify(passwordEncoder, times(1)).encode(memberFormDto.getPassword());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입-중복회원")
    public void register_duplicateMember() {
        // given
        when(memberRepository.findByEmail(memberFormDto.getEmail())).thenReturn(Optional.of(member));

        // when
        // then
        assertThrows(MemberAlreadyExistsException.class, () -> memberService.register(memberFormDto));

        verify(memberRepository, times(1)).findByEmail(memberFormDto.getEmail());
    }

    @Test
    @DisplayName("회원가입-비밀번호불일치")
    public void register_passwordMismatch() {
        // given
        memberFormDto.setConfirm_password("wrong_password");
        when(memberRepository.findByEmail(memberFormDto.getEmail())).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(PasswordMismatchException.class, () -> memberService.register(memberFormDto));

        verify(memberRepository, times(1)).findByEmail(memberFormDto.getEmail());
    }

    @Test
    @DisplayName("회원목록조회-이름검색")
    public void getListByName() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        Member member1 = Member.of("test1@test.com", "test0", "testPassword1");
        Member member2 = Member.of("test2@test.com", "test1", "testPassword2");
        Member member3 = Member.of("test3@test.com", "test2", "testPassword3");
        Page<Member> membersPage = new PageImpl<>(Arrays.asList(member1, member2, member3));

        when(memberRepository.findAllByNameContaining("test", pageable)).thenReturn(membersPage);

        // when
        Page<MemberDto> result = memberService.getListByName("test", pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getName()).isEqualTo("test0");
        assertThat(result.getContent().get(1).getName()).isEqualTo("test1");
        assertThat(result.getContent().get(2).getName()).isEqualTo("test2");

        verify(memberRepository, times(1)).findAllByNameContaining("test", pageable);
    }

    @Test
    @DisplayName("회원조회-성공")
    public void get() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        MemberDto result = memberService.get(memberId);
        result.setId(memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(memberId);
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
        assertThat(result.getName()).isEqualTo(member.getName());

        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    @DisplayName("회원조회-존재하지않는회원")
    public void get_notFound() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(MemberNotFoundException.class, () -> memberService.get(memberId));

        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    @DisplayName("회원수정-성공")
    public void update() {
        // given
        MemberFormDto updateFormDto = MemberFormDto.builder()
                .email(memberFormDto.getEmail())
                .name("updatedName")
                .password("updatedPassword0")
                .confirm_password("updatedPassword0")
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(passwordEncoder.encode(updateFormDto.getPassword())).thenReturn("encodedPassword");

        // when
        MemberDto result = memberService.update(memberId, updateFormDto);
        result.setId(memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(memberId);
        assertThat(result.getEmail()).isEqualTo(updateFormDto.getEmail());
        assertThat(result.getName()).isEqualTo(updateFormDto.getName());

        verify(memberRepository, times(1)).findById(memberId);
        verify(passwordEncoder, times(1)).encode(updateFormDto.getPassword());
    }

    @Test
    @DisplayName("회원수정-존재하지않는회원")
    public void update_notFound() {
        // given
        MemberFormDto updateFormDto = MemberFormDto.builder()
                .email(memberFormDto.getEmail())
                .name("updatedName")
                .password("updatedPassword0")
                .confirm_password("updatedPassword0")
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(MemberNotFoundException.class, () -> memberService.update(memberId, updateFormDto));

        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    @DisplayName("회원수정-본인정보만수정가능")
    public void update_onlySelf() {
        // given
        MemberFormDto updateFormDto = MemberFormDto.builder()
                .email("otherEmail@test.com") // 다른 이메일을 설정
                .name("updatedName")
                .password("updatedPassword0")
                .confirm_password("updatedPassword0")
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        // then
        assertThrows(IllegalArgumentException.class, () -> memberService.update(memberId, updateFormDto));

        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    @DisplayName("회원수정-비밀번호불일치")
    public void update_passwordMismatch() {
        // given
        MemberFormDto updateFormDto = MemberFormDto.builder()
                .email(memberFormDto.getEmail())
                .name("updatedName")
                .password("updatedPassword0")
                .confirm_password("updatedPassword1") // 다른 확인 비밀번호를 설정
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        // then
        assertThrows(PasswordMismatchException.class, () -> memberService.update(memberId, updateFormDto));

        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    @DisplayName("회원수정-동일비밀번호")
    public void update_duplicatePassword() {
        // given
        MemberFormDto updateFormDto = MemberFormDto.builder()
                .email(memberFormDto.getEmail())
                .name("updatedName")
                .password(memberFormDto.getPassword()) // 기존과 동일한 비밀번호를 설정
                .confirm_password(memberFormDto.getPassword())
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(updateFormDto.getPassword(), member.getPassword())).thenReturn(true);

        // when
        // then
        assertThrows(DuplicatePasswordException.class, () -> memberService.update(memberId, updateFormDto));

        verify(memberRepository, times(1)).findById(memberId);
        verify(passwordEncoder, times(1)).matches(updateFormDto.getPassword(), member.getPassword());
    }

    @Test
    @DisplayName("회원수정-비밀번호형식불일치")
    public void update_invalidPassword() {
        // given
        MemberFormDto updateFormDto = MemberFormDto.builder()
                .email(memberFormDto.getEmail())
                .name("updatedName")
                .password("invalidPassword") // 형식에 맞지 않는 비밀번호를 설정
                .confirm_password("invalidPassword")
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        // then
        assertThrows(InvalidPasswordException.class, () -> memberService.update(memberId, updateFormDto));

        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    @DisplayName("회원탈퇴-성공")
    public void withdrawal() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        memberService.withdrawal(memberId);

        // then
        verify(memberRepository, times(1)).findById(memberId);
        verify(memberRepository, times(1)).deleteById(memberId);
    }

    @Test
    @DisplayName("회원탈퇴-존재하지않는회원")
    public void withdrawal_memberNotFound() {
        // given
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(MemberNotFoundException.class, () -> memberService.withdrawal(memberId));

        verify(memberRepository, times(1)).findById(memberId);
    }

}
