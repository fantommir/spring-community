package com.JKS.community.service;

import com.JKS.community.dto.MemberDto;
import com.JKS.community.dto.MemberFormDto;
import com.JKS.community.entity.Member;
import com.JKS.community.exception.InvalidIdException;
import com.JKS.community.exception.InvalidPasswordException;
import com.JKS.community.exception.MemberAlreadyExistsException;
import com.JKS.community.exception.MemberNotFoundException;
import com.JKS.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDto register(MemberFormDto memberFormDto) {
        if (memberRepository.findByLoginId(memberFormDto.getLoginId()).isPresent()) {
            throw new MemberAlreadyExistsException("이미 존재하는 회원입니다.");
        }
        Member member = Member.builder()
                .loginId(memberFormDto.getLoginId())
                .password(passwordEncoder.encode(memberFormDto.getPassword()))
                .name(memberFormDto.getName())
                .build();
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

        // 업데이트할 필드가 존재하면 업데이트
        if (memberFormDto.getName() != null) {
            member.setName(memberFormDto.getName());
        }
        if (memberFormDto.getPassword() != null) {
            member.setPassword(memberFormDto.getPassword());
        }

        return new MemberDto(member);
    }

    @Override
    public void withdrawal(Long memberId) {
        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        memberRepository.deleteById(memberId);
    }
}
