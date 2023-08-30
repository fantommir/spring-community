package com.JKS.community.service;

import com.JKS.community.dto.PostCreateDto;
import com.JKS.community.dto.PostUpdateDto;
import com.JKS.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    // 글 작성
    Post create(PostCreateDto postCreateDto);

    // 글 수정
    Post update(Long postId, PostUpdateDto updatedPostDto);

    // 글 삭제
    void delete(Long postId);

    // 글 상세 조회
    Post get(Long postId);

    // 글 목록 조회 (페이징)
    Page<Post> getList(Pageable pageable);

    Page<Post> getListByCategory(Long categoryId, Pageable pageable);

    // 글 검색
    Page<Post> searchListByKeyword(String keyword, Pageable pageable);

    void react(Long memberId, Long postId, Boolean isLike);

    // TODO 특정 회원 글 조회

    // TODO 인기 글 조회 (카테고리)
}
