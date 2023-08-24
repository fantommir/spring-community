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
    public void registerMember(Member member) {
        if (memberRepository.existsByLoginId(member.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        memberRepository.save(member);
    }

    @Override
    public Member login(String loginId, String password) {
        Optional<Member> member = memberRepository.findByLoginId(loginId);

        if (member.isEmpty()) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        if (!member.get().getPassword().equals(password)) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return member.get();
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
    public Member getMember(Long memberId) {
        if (memberRepository.findById(memberId).isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        return memberRepository.findById(memberId).get();
    }

    @Override
    public Member updateMember(Long memberId, Member updatedMember) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        if (optionalMember.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        Member existingMember = optionalMember.get();

        // 업데이트할 필드가 존재하면 업데이트
        if (updatedMember.getPassword() != null) {
            existingMember.setPassword(updatedMember.getPassword());
        }

        if (updatedMember.getName() != null) {
            existingMember.setName(updatedMember.getName());
        }

        // 다른 필드는 변경하지 않음

        return memberRepository.save(existingMember);
    }

    @Override
    public void withdrawalMember(Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);

        if (member.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        memberRepository.deleteById(memberId);
    }
}
