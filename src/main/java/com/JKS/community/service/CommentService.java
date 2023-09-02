package com.JKS.community.service;

import com.JKS.community.dto.CommentCreateDto;
import com.JKS.community.dto.CommentDto;
import com.JKS.community.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    // 댓글 달기
    CommentDto create(CommentCreateDto commentCreateDto);

    // 댓글 수정
    CommentDto update(Long commentId, String content);

    // 댓글 삭제
    void delete(Long commentId);

    // 댓글 조회
    Page<CommentDto> getList(Long postId, Pageable pageable);

    // 댓글 반응 (좋아요/싫어요)
    CommentDto react(Long memberId, Long commentId, Boolean isLike);

    // 특정 회원 댓글 조회
    Page<CommentDto> getListByMember(Long memberId);

//    TODO: 상위 5개 댓글 조회
//    Page<CommentDto> getTop5(Long postId);
}
