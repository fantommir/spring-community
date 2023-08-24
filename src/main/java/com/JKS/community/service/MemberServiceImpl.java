package com.JKS.community.service;

import com.JKS.community.entity.Member;
import com.JKS.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Member registerMember(Member member) {
        if (!memberRepository.existsByLoginId(member.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
        /* TODO : 비밀번호 암호화 */
//         member.setPassword(passwordEncoder.encode(member.getPassword())); // Spring Security의 PasswordEncoder를 사용한다고 가정
        return memberRepository.save(member);
    }

    @Override
    public Member login(String loginId, String password) {
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        /* TODO : 비밀번호 암호화 */
        // 실제로는 비밀번호 검증을 위한 작업이 필요합니다.
        // Spring Security의 PasswordEncoder를 사용한다고 가정합니다.
        // if (!passwordEncoder.matches(password, member.getPassword())) {
        //     throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        // }
        return member;
    }

    @Override
    public List<Member> searchMembersByName(String name) {
        return memberRepository.findAllByNameContaining(name);
    }

    @Override
    public List<Member> getMemberList() {
        return memberRepository.findAll();
    }

    @Override
    public Optional<Member> getMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    @Override
    public Member updateMember(Long memberId, Member updatedMember) {
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        return memberRepository.save(member.get().updateMember(updatedMember));
    }

    @Override
    public void withdrawalMember(Long memberId) {
        memberRepository.deleteById(memberId);
    }
}
