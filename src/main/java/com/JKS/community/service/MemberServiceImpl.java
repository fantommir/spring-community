package com.JKS.community.service;

import com.JKS.community.dto.MemberDto;
import com.JKS.community.dto.MemberFormDto;
import com.JKS.community.entity.Member;
import com.JKS.community.exception.CustomException;
import com.JKS.community.exception.ErrorCode;
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
            throw new CustomException(ErrorCode.MEMBER_EMAIL_DUPLICATION);
        }

        if (!memberFormDto.getPassword().equals(memberFormDto.getConfirm_password())) {
            throw new CustomException(ErrorCode.MEMBER_PASSWORD_MISMATCH);
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
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return new MemberDto(member);
    }

    @Override
    public MemberDto update(Long memberId, MemberFormDto memberFormDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!member.getEmail().equals(memberFormDto.getEmail())) {
            throw new CustomException(ErrorCode.MEMBER_ACCESS_DENIED);
        }

        String newPassword = memberFormDto.getPassword();
        String confirmPassword = memberFormDto.getConfirm_password();
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                throw new CustomException(ErrorCode.MEMBER_PASSWORD_MISMATCH);
            }
            if (passwordEncoder.matches(newPassword, member.getPassword())) {
                throw new CustomException(ErrorCode.MEMBER_PASSWORD_DUPLICATION);
            }

            Pattern pattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9]).{8,20}$");
            if (!pattern.matcher(newPassword).matches()) {
                throw new CustomException(ErrorCode.MEMBER_PASSWORD_INVALID);
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
                        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        memberRepository.deleteById(memberId);
    }
}
