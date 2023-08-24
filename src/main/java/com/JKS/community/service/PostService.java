package com.JKS.community.service;

import com.JKS.community.entity.Post;

import java.util.List;

public interface PostService {
    // 글 작성
    Post createPost(Post post);

    // 글 수정
    Post updatePost(Long postId, Post updatedPost);

    // 글 삭제
    void deletePost(Long postId);

    // 글 상세 조회
    Post getPost(Long postId);

    // 글 목록 조회
    List<Post> getPostList();

    // 글 검색
    List<Post> searchPostsByKeyword(String keyword);

    // 글 추천 (다시 좋아요를 누르면 취소)
    void toggleLikePost(Long postId);

    // 글 비추천 (다시 싫어요를 누르면 취소)
    void toggleDislikePost(Long postId);
}
