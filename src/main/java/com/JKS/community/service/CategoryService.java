package com.JKS.community.service;

import com.JKS.community.dto.CategoryDto;
import com.JKS.community.dto.CategoryFormDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    // 카테고리 생성
    public CategoryDto create(CategoryFormDto categoryFormDto);

    // 모든 카테고리 목록 조회
    public List<CategoryDto> getList();

    // 특정 카테고리 조회
    public CategoryDto get(Long id);

    // 카테고리 정보 수정
    public void update(Long id, CategoryFormDto categoryFormDto);

    // 특정 카테고리 삭제
    public void delete(Long id);
}