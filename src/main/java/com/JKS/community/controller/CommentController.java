package com.JKS.community.controller;

import com.JKS.community.dto.CommentDto;
import com.JKS.community.dto.CommentFormDto;
import com.JKS.community.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/")
    public ResponseEntity<CommentDto> create(@Valid @RequestBody CommentFormDto commentFormDto) {
        CommentDto createdComment = commentService.create(commentFormDto);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(@PathVariable Long commentId, @Valid @RequestBody String content) {
        CommentDto updatedComment = commentService.update(commentId, content);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<CommentDto>> getCommentsByPost (@PathVariable Long postId, Pageable pageable) {
        Page<CommentDto> commentPage = commentService.getList(postId, pageable);
        return new ResponseEntity<>(commentPage ,HttpStatus.OK);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<Page<CommentDto>> getCommentsByMember (@PathVariable Long memberId, Pageable pageable) {
        Page<CommentDto> commentPage = commentService.getListByMember(memberId ,pageable);
        return new ResponseEntity<>(commentPage, HttpStatus.OK);
    }

    @PostMapping("/{commentId}/reactions")
    public ResponseEntity<CommentDto> react(
            @PathVariable Long commentId,
            @RequestParam("member_id") Long memberId,
            @RequestParam("is_like") Boolean isLike) {
        CommentDto reactedComment = this.commentService.react(memberId, commentId, isLike);
        return new ResponseEntity<>(reactedComment, HttpStatus.CREATED);
    }
}
