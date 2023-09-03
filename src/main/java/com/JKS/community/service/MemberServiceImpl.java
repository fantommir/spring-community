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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public MemberDto register(MemberFormDto memberFormDto) {
        if (memberRepository.findByLoginId(memberFormDto.getLoginId()).isPresent()) {
            throw new MemberAlreadyExistsException("이미 존재하는 회원입니다.");
        }
        Member member = Member.builder()
                .loginId(memberFormDto.getLoginId())
                .password(memberFormDto.getPassword())
                .name(memberFormDto.getName())
                .build();

        // ID를 부여받기 위한 save
        memberRepository.save(member);
        return new MemberDto(member);
    }

    @Override
    public MemberDto login(MemberFormDto memberFormDto) {
        Optional<Member> member = memberRepository.findByLoginId(memberFormDto.getLoginId());

        if (member.isEmpty()) {
            throw new MemberNotFoundException("존재하지 않는 회원입니다.");
        }
        if (!member.get().getLoginId().equals(memberFormDto.getLoginId())) {
            throw new InvalidIdException("아이디가 일치하지 않습니다.");
        }
        if (!member.get().getPassword().equals(memberFormDto.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        return new MemberDto(member.get());
    }

    @Override
    public Page<MemberDto> getListByName(String name, Pageable pageable) {
        List<Member> memberList = memberRepository.findAllByNameContaining(name, pageable).getContent();
        List<MemberDto> memberDtoList = memberList.stream().map(MemberDto::new).toList();
        return new PageImpl<>(memberDtoList, pageable, memberDtoList.size());
    }

    @Override
    public Page<MemberDto> getList(Pageable pageable) {
        List<Member> memberList = memberRepository.findAll(pageable).getContent();
        List<MemberDto> memberDtoList = memberList.stream().map(MemberDto::new).toList();
        return new PageImpl<>(memberDtoList, pageable, memberDtoList.size());
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
