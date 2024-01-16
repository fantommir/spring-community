package com.JKS.community.controller;

import com.JKS.community.dto.PageRequestDto;
import com.JKS.community.dto.PostDto;
import com.JKS.community.dto.PostFormDto;
import com.JKS.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "PostController", description = "게시글 관련 API")
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "게시글 생성", description = "게시글을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시글 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
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
        PostDto postDto = postService.get(postId);
        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }

    @GetMapping
    public Page<PostDto> getList(Pageable pageable) {
        return postService.getList(pageable);
    }

    @GetMapping("/category/{categoryId}")
    public Page<PostDto> getListByCategory(@PathVariable Long categoryId, PageRequestDto pageRequest) {
        Pageable pageable = pageRequest.toPageable();
        return postService.getListByCategory(categoryId, pageable);
    }


    // 특정 카테고리의 child 카테고리 게시글 목록 조회
    @GetMapping("/category/{categoryId}/subcategories")
    public Page<PostDto> getListByChildCategory(@PathVariable Long categoryId, PageRequestDto pageRequest) {
        Pageable pageable = pageRequest.toPageable();
        return postService.getListByParentCategory(categoryId, pageable);
    }

    // 특정 회원의 게시글 목록 조회
    @GetMapping("/member/{memberId}")
    public Page<PostDto> getListByMember(@PathVariable Long memberId, PageRequestDto pageRequest) {
        Pageable pageable = pageRequest.toPageable();
        return postService.getListByMember(memberId, pageable);
    }

    // 검색어를 포함하는 게시글 목록 조회
    @GetMapping("/search")
    public Page<PostDto> searchListByKeyword(String keyword, Pageable pageable) {
        return this.postService.searchListByKeyword(keyword, pageable);
    }

    // 게시물 좋아요/싫어요 반응 생성 및 수정
    @PostMapping("/{postId}/react")
    public ResponseEntity<PostDto> react(
            @RequestParam("member_id") Long memberId,
            @RequestParam("is_like") Boolean isLike,
            @PathVariable Long postId) {
        try {
            PostDto reactedPost = this.postService.react(memberId, postId, isLike);
            return new ResponseEntity<>(reactedPost, HttpStatus.CREATED);
        } catch(Exception e){
            // Print stack trace for any exception thrown.
            System.out.println("e = " + e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
