package com.JKS.community.service;

import com.JKS.community.dto.PostCreateDto;
import com.JKS.community.dto.PostUpdateDto;
import com.JKS.community.entity.*;
import com.JKS.community.repository.CategoryRepository;
import com.JKS.community.repository.MemberRepository;
import com.JKS.community.repository.PostRepository;
import com.JKS.community.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ReactionRepository reactionRepository;

    @Override
    // 게시글을 생성하는 메서드
    public Post createPost(PostCreateDto postCreateDto) {
        // 1. memberId와 categoryId를 이용하여 Member와 Category 엔티티를 검색
        // 해당 아이디를 가진 Member 혹은 Category가 없다면 예외 발생
        Member member = memberRepository.findById(postCreateDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid memberId:" + postCreateDto.getMemberId()));

        Category category = categoryRepository.findById(postCreateDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid categoryId:" + postCreateDto.getCategoryId()));

        // 2. 검색한 Member와 Category 엔티티를 이용하여 새로운 Post 엔티티 생성
        Post newPost = Post.builder()
                .title(postCreateDto.getTitle())
                .content(postCreateDto.getContent())
                .member(member)
                .category(category)
                .build();

        // 3. 생성된 Post 엔티티를 저장하고 반환
        return postRepository.save(newPost);
    }

    @Override
    // 게시글을 수정하는 메서드
    public Post updatePost(Long postId, PostUpdateDto updatedPostDto) {
        // postId로 Post 엔티티를 검색
        // 해당 아이디를 가진 Post가 없다면 예외 발생
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));

        // 검색한 Post 엔티티의 정보를 수정
        existingPost.update(updatedPostDto);

        // 수정된 Post 엔티티를 저장하고 반환
        return postRepository.save(existingPost);
    }

    @Override
    // 게시글을 삭제하는 메서드
    public void deletePost(Long postId) {
        // postId로 Post 엔티티를 검색
        // 해당 아이디를 가진 Post가 없다면 예외 발생
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));

        // 검색한 Post 엔티티를 비활성화
        existingPost.disable();
    }

    @Override
    // 게시글을 조회하는 메서드 (enabled가 true인 경우만 조회)
    public Post getPost(Long postId) {
        // postId로 Post 엔티티를 검색
        // 해당 아이디를 가진 Post가 없다면 예외 발생
        Post existingPost = postRepository.findByEnabledTrueAndId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));

        // 검색한 Post 엔티티의 조회수를 증가시킴
        existingPost.increaseViewCount();

        postRepository.save(existingPost);

        // 조회수가 증가된 Post 엔티티를 반환
        return existingPost;
    }

    @Override
    // 모든 게시글을 페이지에 맞게 검색하는 메서드
    public Page<Post> getPostList(Pageable pageable) {
        return postRepository.findAllByEnabledTrue(pageable);
    }

    @Override
    // 키워드를 포함하는 게시글을 페이지에 맞게 검색하는 메서드 (enabled가 true인 경우만 조회)
    public Page<Post> searchPostsByKeyword(String keyword, Pageable pageable) {
        return postRepository.searchActivePostsByKeyword(keyword, pageable);
    }

    @Override
    // 게시글에 대한 반응을 저장하는 메서드
    public void reactPost(Long memberId, Long postId, boolean isLike) {
        // memberId와 postId로 Member와 Post 엔티티를 검색
        // 해당 아이디를 가진 Member 혹은 Post가 없다면 예외 발생
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("No member found with id " + memberId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("No post found with id " + postId));

        // 해당 Member가 해당 Post에 기존 반응이 있는지 확인
        Optional<Reaction> optionalReaction = reactionRepository.findByMemberIdAndPostId(memberId, postId);

        // 이미 반응이 있다면 예외 발생
        if (optionalReaction.isPresent()) {
            throw new IllegalArgumentException("You've already reacted to this post.");
        }

        // 새로운 Reaction을 생성하고 저장
        Reaction newReaction = Reaction.of(member, post, isLike);
        reactionRepository.save(newReaction);

        // 이 Post에 대한 좋아요 혹은 싫어요 카운트 증가
        if(isLike){
            post.increaseLikeCount();
        }else{
            post.increaseDislikeCount();
        }
    }
}
