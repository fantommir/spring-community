package com.JKS.community.service;

import com.JKS.community.dto.CategoryDto;
import com.JKS.community.dto.CategoryFormDto;

import java.util.List;

public interface CategoryService {
    // 카테고리 생성
    CategoryDto create(CategoryFormDto categoryFormDto);

    // 모든 카테고리 목록 조회
    List<CategoryDto> getList();

    // 특정 카테고리 조회
    CategoryDto get(Long id);

    // 카테고리 정보 수정
    void update(Long id, CategoryFormDto categoryFormDto);

    // 특정 카테고리 삭제
    void delete(Long id);
}