package com.JKS.community.service;

import com.JKS.community.dto.PostCreateDto;
import com.JKS.community.dto.PostUpdateDto;
import com.JKS.community.entity.Category;
import com.JKS.community.entity.Member;
import com.JKS.community.entity.Post;
import com.JKS.community.repository.CategoryRepository;
import com.JKS.community.repository.MemberRepository;
import com.JKS.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Post createPost(PostCreateDto postCreateDto) {
        // 1. memberId와 categoryId로부터 실제 Member와 Category 엔티티 참조 획득
        Member member = memberRepository.findById(postCreateDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid memberId:" + postCreateDto.getMemberId()));

        Category category = categoryRepository.findById(postCreateDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid categoryId:" + postCreateDto.getCategoryId()));

        // 2. Post 엔티티 생성
        Post newPost = Post.builder()
                .title(postCreateDto.getTitle())
                .content(postCreateDto.getContent())
                .member(member)
                .category(category)
                .build();

        // 3. 생성된 엔티티 저장 및 반환
        return postRepository.save(newPost);
    }

    @Override
    public Post updatePost(Long postId, PostUpdateDto updatedPostDto) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));

        existingPost.update(updatedPostDto);

        return postRepository.save(existingPost);
    }

    @Override
    public void deletePost(Long postId) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));

        postRepository.delete(existingPost);
    }

    @Override
    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));
    }

    @Override
    public Page<Post> getPostList(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public Page<Post> searchPostsByKeyword(String keyword, Pageable pageable) {
        return postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    }

    @Override
    public void toggleLikePost(Long userId, Long postId) {

    }

    @Override
    public void toggleDislikePost(Long userId, Long postId) {

    }
}
