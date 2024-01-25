package com.JKS.community.repository;

import com.JKS.community.dto.MemberDto;
import com.JKS.community.dto.MemberSearchCondition;
import com.JKS.community.dto.QMemberDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.JKS.community.entity.QMember.member;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<MemberDto> search(MemberSearchCondition condition) {
        return queryFactory
                .select(new QMemberDto(
                        member.id,
                        member.name
                ))
                .from(member)
                .where(
                        memberIdEq(condition.getId()),
                        memberNameEq(condition.getName())
                )
                .fetch();
    }

    private BooleanExpression memberIdEq(Long id) {
        return id != null ? member.id.eq(id) : null;
    }

    private BooleanExpression memberNameEq(String name) {
        return name != null ? member.name.eq(name) : null;
    }
}
