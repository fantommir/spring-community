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
    public Post create(PostCreateDto postCreateDto) {
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
    public Post update(Long postId, PostUpdateDto updatedPostDto) {
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
    public void delete(Long postId) {
        // postId로 Post 엔티티를 검색
        // 해당 아이디를 가진 Post가 없다면 예외 발생
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));

        // 검색한 Post 엔티티를 비활성화
        existingPost.disable();

        // TODO: 게시글에 달린 댓글들도 비활성화
    }

    @Override
    // 게시글을 조회하는 메서드 (enabled가 true인 경우만 조회)
    public Post get(Long postId) {
        // postId로 Post 엔티티를 검색
        // 해당 아이디를 가진 Post가 없다면 예외 발생
        Post existingPost = postRepository.findByEnabledTrueAndId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));

        // 검색한 Post 엔티티의 조회수를 증가시킴
        existingPost.increaseViewCount();

        // 조회수가 증가된 Post 엔티티를 반환
        return existingPost;
    }

    @Override
    // 모든 게시글을 페이지에 맞게 검색하는 메서드
    public Page<Post> getList(Pageable pageable) {
        return postRepository.findAllByEnabledTrue(pageable);
    }

    @Override
    public Page<Post> getListByCategory(Long categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Invalid category ID: " + categoryId);
        }
        return postRepository.findAllByEnabledTrueAndCategoryId(categoryId, pageable);
    }


    @Override
    public Page<Post> searchListByKeyword(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("invalid keyword");
        }
        return postRepository.searchActivePostsByKeyword(keyword, pageable);
    }

    @Override
    public void react(Long memberId, Long postId, Boolean isLike) {
        // Check if memberId and postId are valid.
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + memberId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));

        // Check if the user has already reacted to the post.
        Optional<Reaction> optionalReaction = reactionRepository.findByMemberIdAndPostId(memberId, postId);

        Reaction existingReaction = optionalReaction.orElse(null);
        if (existingReaction == null) {
            // If there's no existing reaction, create a new one.
            Reaction newReaction = Reaction.of(member, post, isLike);
            reactionRepository.save(newReaction);

            // Increase the corresponding count in the post.
            if (isLike) {
                post.setLikeCount(post.getLikeCount() + 1);
            } else {
                post.setDislikeCount(post.getDislikeCount() + 1);
            }
        } else if (existingReaction.isLike() != isLike) {
            // If there's an existing reaction and the type is different,
            // update the reaction and adjust the counts in the post.
            existingReaction.update(isLike);

            if (isLike) {
                post.setLikeCount(post.getLikeCount() + 1);
                post.setDislikeCount(post.getDislikeCount() - 1);
            } else {
                post.setDislikeCount(post.getDislikeCount() + 1);
                post.setLikeCount(post.getLikeCount() - 1);
            }
        }

        // Save the updated post.
        postRepository.save(post);
    }
}
