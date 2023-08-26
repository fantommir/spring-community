package com.JKS.community.service;

import com.JKS.community.dto.PostCreateDto;
import com.JKS.community.dto.PostUpdateDto;
import com.JKS.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    // 글 작성
    Post createPost(PostCreateDto postCreateDto);

    // 글 수정
    Post updatePost(Long postId, PostUpdateDto updatedPostDto);

    // 글 삭제
    void deletePost(Long postId);

    // 글 상세 조회
    Post getPost(Long postId);

    // 글 목록 조회 (페이징)
    Page<Post> getPostList(Pageable pageable);

    // 글 검색
    Page<Post> searchPostsByKeyword(String keyword, Pageable pageable);

    void reactPost(Long memberId, Long postId, boolean isLike);

}
