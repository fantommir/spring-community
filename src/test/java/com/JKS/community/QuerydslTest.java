package com.JKS.community;

import com.JKS.community.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.JKS.community.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
public class QuerydslTest {

    @Autowired
    EntityManager em;

    @Test
    public void start() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.email.eq("rltjd8714@gmail.com"))
                .fetchOne();

        assertThat(findMember.getEmail()).isEqualTo("rltjd8714@gmail.com");
    }

    @Test
    public void search() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.email.eq("rltjd8714@gmail.com")
                        .and(member.name.eq("KSJUNG")))
                .fetchOne();

        assertThat(findMember.getEmail()).isEqualTo("rltjd8714@gmail.com");
        assertThat(findMember.getName()).isEqualTo("KSJUNG");
    }

    @Test
    public void searchAndParam() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        //List
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        //처음 한 건 조회
        Member findMember2 = queryFactory
                .selectFrom(member)
                .fetchFirst();
        //페이징에서 사용
        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch();

        //count 쿼리로 변경
        Long count = queryFactory
                .select(member.count())
                .from(member)
                .fetchOne(); // 응답은 숫자 하나
    }

    @Test
    public void sort() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<Member> result1 = queryFactory
                .selectFrom(member)
                .orderBy(member.email.desc())
                .fetch();

        List<Member> result2 = queryFactory
                .selectFrom(member)
                .orderBy(member.email.desc(), member.name.asc().nullsLast())
                .fetch();
    }
}
