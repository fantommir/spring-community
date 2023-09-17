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
        System.out.println("memberEmail = " + member.getEmail());
        System.out.println("memberName = " + member.getName());
        System.out.println("memberPassword = " + member.getPassword());
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

        member.update(memberFormDto.getName(), passwordEncoder.encode(memberFormDto.getPassword()));

        return new MemberDto(member);
    }

    @Override
    public void withdrawal(Long memberId) {
        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        memberRepository.deleteById(memberId);
    }
}
