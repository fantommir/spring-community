package com.JKS.community.service;

import com.JKS.community.dto.PostDto;
import com.JKS.community.dto.PostFormDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    // 글 작성
    PostDto create(PostFormDto postFormDto);

    // 글 수정
    PostDto update(Long postId, PostFormDto postFormDto);

    // 글 삭제
    void delete(Long postId);

    // 글 상세 조회
    PostDto get(Long postId);

    // 글 목록 조회 (페이징)
    Page<PostDto> getList(Pageable pageable);

    Page<PostDto> getListByCategory(Long categoryId, Pageable pageable);

    Page<PostDto> getListByParentCategory(Long parentCategoryId, Pageable pageable);

    Page<PostDto> getListByMember(Long memberId, Pageable pageable);

    // 글 검색
    Page<PostDto> searchListByKeyword(String keyword, Pageable pageable);

    PostDto react(Long memberId, Long postId, Boolean isLike);

    void increaseViewCount(Long postId);

    // TODO 특정 회원 글 조회

    // TODO 인기 글 조회 (카테고리)

    long countPostsByMember(Long memberId);
}
