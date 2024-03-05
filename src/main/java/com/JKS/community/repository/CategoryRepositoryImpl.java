package com.JKS.community.repository;

import com.JKS.community.entity.Category;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.JKS.community.entity.QCategory.category;

public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CategoryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Category> findAllByDepth(int depth) {
        return queryFactory
                .selectFrom(category)
                .where(category.depth.eq(depth))
                .fetch();
    }
}
