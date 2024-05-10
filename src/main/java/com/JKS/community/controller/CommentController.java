package com.JKS.community.controller;

import com.JKS.community.dto.CommentDto;
import com.JKS.community.dto.CommentFormDto;
import com.JKS.community.dto.PageRequestDto;
import com.JKS.community.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "댓글 API")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/")
    @Operation(summary = "댓글 생성")
    public ResponseEntity<CommentDto> create(@Valid @RequestBody CommentFormDto commentFormDto) {
        CommentDto createdComment = commentService.create(commentFormDto);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정")
    public ResponseEntity<CommentDto> update(@PathVariable Long commentId, @Valid @RequestBody String content) {
        CommentDto updatedComment = commentService.update(commentId, content);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/post/{postId}")
    @Operation(summary = "게시글의 댓글 조회")
    public ResponseEntity<Page<CommentDto>> getRootCommentsByPost (@PathVariable Long postId, PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestDto.toPageable();
        Page<CommentDto> commentPage = commentService.getRootCommentsByPost(postId, pageable);
        return new ResponseEntity<>(commentPage ,HttpStatus.OK);
    }

    @GetMapping("/post/{postId}/totalPages")
    @Operation(summary = "게시글의 댓글 페이지 수 조회")
    public ResponseEntity<Integer> getTotalPagesByPost (@PathVariable Long postId, PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestDto.toPageable();
        Page<CommentDto> commentPage = commentService.getRootCommentsByPost(postId, pageable);
        return new ResponseEntity<>(commentPage.getTotalPages() ,HttpStatus.OK);
    }

    @GetMapping("/{commentId}/replies")
    @Operation(summary = "대댓글 조회")
    public ResponseEntity<Page<CommentDto>> getListByParent (@PathVariable Long commentId, PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestDto.toPageable();
        Page<CommentDto> commentPage = commentService.getCommentByParent(commentId, pageable);
        return new ResponseEntity<>(commentPage ,HttpStatus.OK);
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "회원의 댓글 조회")
    public ResponseEntity<Page<CommentDto>> getListByMember (@PathVariable Long memberId, Pageable pageable) {
        Page<CommentDto> commentPage = commentService.getListByMember(memberId ,pageable);
        return new ResponseEntity<>(commentPage, HttpStatus.OK);
    }

    @PostMapping("/{commentId}/reactions")
    @Operation(summary = "댓글 반응")
    public ResponseEntity<CommentDto> react(
            @PathVariable Long commentId,
            @RequestParam("member_id") Long memberId,
            @RequestParam("is_like") Boolean isLike) {
        CommentDto reactedComment = this.commentService.react(memberId, commentId, isLike);
        return new ResponseEntity<>(reactedComment, HttpStatus.CREATED);
    }
}
