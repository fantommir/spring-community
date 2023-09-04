package com.JKS.community.controller;

import com.JKS.community.dto.PostDto;
import com.JKS.community.dto.PostFormDto;
import com.JKS.community.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto> create(@Valid @RequestBody PostFormDto postFormDto) {
        PostDto createdPost = postService.create(postFormDto);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> update(@PathVariable Long postId,
                                          @Valid @RequestBody PostFormDto postFormDto) {
        PostDto updatedPost = postService.update(postId, postFormDto);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        postService.delete(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> get(@PathVariable Long postId) {
        PostDto foundPost = postService.get(postId);
        return new ResponseEntity<>(foundPost, HttpStatus.OK);
    }

    // 페이지 번호와 페이지 크기를 파라미터로 받아 게시글 목록 조회
    // 예: /posts?page=0&size=10
    // page는 0부터 시작하며 size는 한 페이지에 표시할 게시글 수를 의미합니다.
    // 이 값들은 Spring Data JPA의 Pageable 인터페이스를 통해 자동으로 처리됩니다.

    @GetMapping
    public Page<PostDto> getList(Pageable pageable) {
        return postService.getList(pageable);
    }

    // 특정 카테고리의 게시글 목록 조회
    @GetMapping("/category/{categoryId}")
    public Page<PostDto> getListByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return postService.getListByCategory(categoryId, pageable);
    }

    // 검색어를 포함하는 게시글 목록 조회
    @GetMapping("/search")
    public Page<PostDto> searchListByKeyword(String keyword, Pageable pageable) {
        return this.postService.searchListByKeyword(keyword, pageable);
    }

    // 게시물 좋아요/싫어요 반응 생성 및 수정
    @PostMapping("/{postId}/reactions")
    public ResponseEntity<PostDto> react(
            @RequestParam("member_id") Long memberId,
            @RequestParam("is_like") Boolean isLike,
            @PathVariable Long postId) {
        PostDto reactedPost = this.postService.react(memberId, postId, isLike);
        return new ResponseEntity<>(reactedPost, HttpStatus.CREATED);
    }
}
