package com.JKS.community.service;

import com.JKS.community.dto.PostCreateDto;
import com.JKS.community.dto.PostUpdateDto;
import com.JKS.community.entity.Category;
import com.JKS.community.entity.Member;
import com.JKS.community.entity.Post;
import com.JKS.community.repository.CategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class PostServiceImplTest {

    @Autowired
    private PostService postService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CategoryRepository categoryRepository;

    private Member newMember;
    private Category childCategory;

    @BeforeEach
    void setUp() {
        newMember = Member.builder()
                .loginId("loginId")
                .password("password")
                .name("name").build();
        memberService.registerMember(newMember);

        Category parentCategory = new Category("parent", null);
        childCategory = new Category("child", parentCategory);
        categoryRepository.save(parentCategory);
        categoryRepository.save(childCategory);
    }

    @Test
    void createPost_Success() {
        // given
        PostCreateDto postCreateDto = PostCreateDto.builder()
                .title("title")
                .content("content")
                .memberId(newMember.getId())
                .categoryId(childCategory.getId()).build();

        // when
        Post newPost = postService.create(postCreateDto);

        // then
        Post findPost = postService.get(newPost.getId());

        assertThat(findPost).usingRecursiveComparison().isEqualTo(newPost);

        // Verify that other fields are not changed.
        assertThat(findPost.getViewCount()).isOne();
    }

    @Test
    void createPost_Failure() {
        // given
        Long invalidMemberId=-1L;
        PostCreateDto postCreateDto=  PostCreateDto.builder()
                .title("title")
                .content("content")
                .memberId(invalidMemberId)
                .categoryId(childCategory.getId()).build();

        // fail: create post with invalid memberId.
        assertThatThrownBy(() -> {postService.create(postCreateDto);})
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(String.valueOf(invalidMemberId));

        // fail: get post with invalid postId.
        assertThatThrownBy(() -> {postService.get(-1L);})
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("-1");
    }

    @Test
    public void update_Success() throws Exception {
        String title="title";
        String content="content";

        PostCreateDto createDTO=  PostCreateDto.builder()
                .title(title)
                .content(content)
                .memberId(newMember.getId())
                .categoryId(childCategory.getId()).build();

        PostUpdateDto updateDTO = new PostUpdateDto(title+"updated", content+"updated");

        Post created = postService.create(createDTO);

        created = postService.update(created.getId(),updateDTO);

        assertThat(created.getTitle()).isEqualTo(updateDTO.getTitle());
        assertThat(created.getContent()).isEqualTo(updateDTO.getContent());
    }

    @Test
    public void update_Failure() throws Exception {
        String title="title";
        String content="content";

        PostUpdateDto updateDTO = new PostUpdateDto(title+"updated", content+"updated");

        // fail: update post with invalid postId.
        assertThatThrownBy(()->{postService.update(-1L,updateDTO);})
            .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("-1");
    }
}