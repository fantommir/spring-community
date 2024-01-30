package com.JKS.community.repository;

import com.JKS.community.dto.MemberDto;
import com.JKS.community.dto.MemberSearchCondition;
import com.JKS.community.dto.QMemberDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.JKS.community.entity.QMember.member;
import static org.springframework.util.StringUtils.hasText;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<MemberDto> search(MemberSearchCondition condition, Pageable pageable) {
        List<MemberDto> content = queryFactory
                .select(new QMemberDto(
                        member.id,
                        member.name
                ))
                .from(member)
                .where(
                        memberIdEq(condition.getId()),
                        memberNameEq(condition.getName())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .where(
                        memberIdEq(condition.getId()),
                        memberNameEq(condition.getName())
                );

        // PageableExecutionUtils.getPage는 실제로 데이터를 가져오는 쿼리와,
        // 총 개수를 계산하는 쿼리를 받아서 필요할 경우에만 총 개수 쿼리를 실행시킵니다.
        // 이는 성능 최적화에 도움을 줍니다.
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression memberIdEq(Long id) {
        return id != null ? member.id.eq(id) : null;
    }

    private BooleanExpression memberNameEq(String name) {
        return hasText(name) ? member.name.eq(name) : null;
    }
}
