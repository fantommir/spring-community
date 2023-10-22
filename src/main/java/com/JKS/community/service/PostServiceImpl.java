package com.JKS.community.service;

import com.JKS.community.dto.PostDto;
import com.JKS.community.dto.PostFormDto;
import com.JKS.community.entity.Category;
import com.JKS.community.entity.Member;
import com.JKS.community.entity.Post;
import com.JKS.community.entity.Reaction;
import com.JKS.community.exception.CategoryNotFoundException;
import com.JKS.community.exception.MemberNotFoundException;
import com.JKS.community.exception.PostNotFoundException;
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
    public PostDto create(PostFormDto postFormDto) {
        Member member = memberRepository.findById(postFormDto.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("Invalid memberId:" + postFormDto.getMemberId()));

        Category category = categoryRepository.findById(postFormDto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Invalid categoryId:" + postFormDto.getCategoryId()));

        Post newPost = Post.of(postFormDto.getTitle(), postFormDto.getContent(), member, category);

        postRepository.save(newPost);
        return new PostDto(newPost);
    }

    @Override
    public PostDto update(Long postId, PostFormDto postFormDto) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Invalid post Id:" + postId));

        if (!existingPost.getMember().getId().equals(postFormDto.getMemberId())) {
            throw new PostNotFoundException("게시글은 작성자 본인만 수정할 수 있습니다.");
        }

        Category category = categoryRepository.findById(postFormDto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Invalid categoryId:" + postFormDto.getCategoryId()));

        existingPost.update(postFormDto.getTitle(), postFormDto.getContent(), category);

        postRepository.save(existingPost);
        return new PostDto(existingPost);
    }

    @Override
    public void delete(Long postId) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Invalid post Id:" + postId));

        postRepository.delete(existingPost);
    }

    @Override
    public PostDto get(Long postId) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Invalid post Id:" + postId));
        return new PostDto(existingPost);
    }

    @Override
    public Page<PostDto> getList(Pageable pageable) {
        return postRepository.findAll(pageable).map(PostDto::new);
    }

    @Override
    public Page<PostDto> getListByCategory(Long categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("Invalid category ID: " + categoryId);
        }
        return postRepository.findAllByCategoryId(categoryId, pageable).map(PostDto::new);
    }

    @Override
    public Page<PostDto> getListByParentCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Invalid category ID: " + categoryId));

        List<Long> childCategoryIds = category.getChildren().stream()
                .map(Category::getId)
                .toList();

        return postRepository.findAllByCategoryIdIn(childCategoryIds, pageable).map(PostDto::new);
    }

    @Override
    public Page<PostDto> getListByMember(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Invalid member ID: " + memberId));
        return postRepository.findAllByMemberId(member.getId(), pageable).map(PostDto::new);
    }

    @Override
    public Page<PostDto> searchListByKeyword(String keyword, Pageable pageable) {
        return postRepository.searchActivePostsByKeyword(keyword, pageable).map(PostDto::new);
    }

    @Override
    public PostDto react(Long memberId, Long postId, Boolean isLike) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Invalid member ID: " + memberId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Invalid post ID: " + postId));

        Optional<Reaction> optionalReaction = reactionRepository.findByMemberIdAndPostId(memberId, postId);
        if (optionalReaction.isPresent()) {
            Reaction existingReaction = optionalReaction.get();
            if (existingReaction.isLike() == isLike) {
                // Cancel reaction
                reactionRepository.delete(existingReaction);
                post.setLikeCount(post.getLikeCount() - (isLike ? 1 : 0));
                post.setDislikeCount(post.getDislikeCount() - (isLike ? 0 : 1));
            } else {
                // If the user changes their reaction, update it and adjust the counts.
                existingReaction.update(isLike);
                post.setLikeCount(post.getLikeCount() + (isLike ? 1 : -1));
                post.setDislikeCount(post.getDislikeCount() - (isLike ? 1 : -1));
            }
        } else {
            // If there's no existing reaction, create a new one.
            Reaction newReaction = Reaction.of(member,post, isLike);
            reactionRepository.save(newReaction);
            post.setLikeCount(post.getLikeCount()+(isLike ? 1 : 0));
            post.setDislikeCount(post.getDislikeCount()+(isLike ? 0 : 1));
        }

        return new PostDto(post);
    }

    // 게시물 조회수 증가
    @Override
    public void increaseViewCount(Long postId) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Invalid post Id:" + postId));
        existingPost.increaseViewCount();
    }

    @Override
    public long countPostsByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
        return memberRepository.countPostsByMember(member);
    }
}
