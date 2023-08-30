package com.JKS.community.service;

import com.JKS.community.dto.CommentCreateDto;
import com.JKS.community.entity.Comment;

public interface CommentService {
    // 댓글 달기
    Comment create(CommentCreateDto commentCreateDto);

    // 댓글 수정
    Comment update(Long commentId, String content);

    // 댓글 삭제
    void delete(Long commentId);

    // 댓글 조회
    void getList(Long postId);

    // 댓글 반응 (좋아요/싫어요)
    void react(Long commentId, Long memberId, boolean isLike);

    // 특정 회원 댓글 조회
    void getListByMember(Long memberId);

    // 상위 5개 댓글 조회
    void getTop5(Long postId);
}
