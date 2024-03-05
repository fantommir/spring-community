package com.JKS.community.repository;

import com.JKS.community.entity.Category;

import java.util.List;

public interface CategoryRepositoryCustom {
    List<Category> findAllByDepth(int depth);
}