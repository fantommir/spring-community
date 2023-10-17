package com.JKS.community.service;

import com.JKS.community.dto.MemberDto;
import com.JKS.community.dto.MemberFormDto;
import com.JKS.community.entity.Member;
import com.JKS.community.exception.MemberAlreadyExistsException;
import com.JKS.community.exception.MemberNotFoundException;
import com.JKS.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDto register(MemberFormDto memberFormDto) {
        if (memberRepository.findByEmail(memberFormDto.getEmail()).isPresent()) {
            throw new MemberAlreadyExistsException("이미 존재하는 회원입니다.");
        }
        Member member = Member.of(memberFormDto.getEmail(), memberFormDto.getName(), passwordEncoder.encode(memberFormDto.getPassword()));
        return new MemberDto(memberRepository.save(member));
    }

    @Override
    public Page<MemberDto> getListByName(String name, Pageable pageable) {
        return memberRepository.findAllByNameContaining(name, pageable).map(MemberDto::new);
    }

    @Override
    public Page<MemberDto> getList(Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberDto::new);
    }

    @Override
    public MemberDto get(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
        return new MemberDto(member);
    }

    @Override
    public MemberDto update(Long memberId, MemberFormDto memberFormDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        if (!member.getEmail().equals(memberFormDto.getEmail())) {
            throw new IllegalArgumentException("본인의 정보만 수정할 수 있습니다.");
        }

        String newPassword = memberFormDto.getPassword();
        String confirmPassword = memberFormDto.getConfirm_password();
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
            if (passwordEncoder.matches(newPassword, member.getPassword())) {
                throw new IllegalArgumentException("동일한 비밀번호로 변경할 수 없습니다.");
            }
            if (passwordEncoder.matches(newPassword, member.getPassword())) {
                throw new IllegalArgumentException("동일한 비밀번호로 변경할 수 없습니다.");
            }

            Pattern pattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9]).{8,20}$");
            if (!pattern.matcher(newPassword).matches()) {
                throw new IllegalArgumentException("비밀번호는 알파벳과 숫자를 포함한 8~20자리여야 합니다.");
            }
            newPassword = passwordEncoder.encode(newPassword);
        } else {
            newPassword = member.getPassword();
        }
        member.update(memberFormDto.getName(), newPassword);

        return new MemberDto(member);
    }


    @Override
    public void withdrawal(Long memberId) {
        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        memberRepository.deleteById(memberId);
    }
}
