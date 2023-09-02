package com.JKS.community.service;

import com.JKS.community.dto.PostCreateDto;
import com.JKS.community.dto.PostDto;
import com.JKS.community.dto.PostFormDto;
import com.JKS.community.dto.PostUpdateDto;
import com.JKS.community.entity.Category;
import com.JKS.community.entity.Member;
import com.JKS.community.entity.Post;
import com.JKS.community.exception.CategoryNotFoundException;
import com.JKS.community.exception.MemberNotFoundException;
import com.JKS.community.exception.PostNotFoundException;
import com.JKS.community.repository.CategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        PostFormDto formDto = PostFormDto.builder()
                .title("title")
                .content("content")
                .memberId(newMember.getId())
                .categoryId(childCategory.getId())
                .build();

        // when
        PostDto created = postService.create(formDto);

        // then
        PostDto findPostDto = postService.get(created.getId());

        assertThat(findPostDto.getTitle()).isEqualTo(formDto.getTitle());
        assertThat(findPostDto.getContent()).isEqualTo(formDto.getContent());
    }

    @Test
    void createPost_Failure() {
        // given
        Long invalidMemberId=-1L;
        PostFormDto formDto = PostFormDto.builder()
                .title("title")
                .content("content")
                .memberId(invalidMemberId)
                .categoryId(childCategory.getId())
                .build();

        // fail: create post with invalid memberId.
        assertThatThrownBy(() -> {postService.create(formDto);})
            .isInstanceOf(MemberNotFoundException.class)
            .hasMessageContaining(String.valueOf(invalidMemberId));

        // fail: get post with invalid postId.
        assertThatThrownBy(() -> {postService.get(-1L);})
            .isInstanceOf(PostNotFoundException.class)
            .hasMessageContaining("-1");
    }

    @Test
    public void update_Success() throws Exception {
        PostFormDto formDto = PostFormDto.builder()
                .title("title")
                .content("content")
                .memberId(newMember.getId())
                .categoryId(childCategory.getId())
                .build();

        PostDto created = postService.create(formDto);

        String updatedTitle = "updated title";
        formDto.setTitle(updatedTitle);
        String updatedContent = "updated content";
        formDto.setContent(updatedContent);
        PostDto updated = postService.update(created.getId(), formDto);

        assertThat(updatedTitle).isEqualTo(updated.getTitle());
        assertThat(updatedContent).isEqualTo(updated.getContent());
    }

    @Test
    public void update_Failure() throws Exception {
        PostFormDto formDto = new PostFormDto();
        formDto.setTitle("updated title");
        formDto.setContent("updated content");

        // fail: update post with invalid postId.
        assertThatThrownBy(()->{postService.update(-1L,formDto);})
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("-1");
    }

    @Test
    public void delete_Success() throws Exception {
        PostFormDto formDto = PostFormDto.builder()
                .title("title")
                .content("content")
                .memberId(newMember.getId())
                .categoryId(childCategory.getId())
                .build();

        PostDto created = postService.create(formDto);

        postService.delete(created.getId());

        assertThatThrownBy(()->{postService.get(created.getId());})
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining(String.valueOf(created.getId()));
    }

    @Test
    public void delete_Failure() throws Exception {
        // fail: delete post with invalid postId.
        assertThatThrownBy(()->{postService.delete(-1L);})
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("-1");
    }

    @Test
    public void get_Success() throws Exception {
        PostFormDto formDto = PostFormDto.builder()
                .title("title")
                .content("content")
                .memberId(newMember.getId())
                .categoryId(childCategory.getId())
                .build();

        PostDto created = postService.create(formDto);

        PostDto postDto = postService.get(created.getId());

        assertThat(postDto.getTitle()).isEqualTo("title");
        assertThat(postDto.getContent()).isEqualTo("content");
    }

    @Test
    public void get_Failure() throws Exception {
        assertThatThrownBy(()->{postService.get(-1L);})
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("-1");
    }

    @Test
    public void getList_Success() throws Exception {

        for(int i=0; i<11; i++) {
            String title="title" + i;
            String content="content" + i;

            PostFormDto formDto = PostFormDto.builder()
                    .title(title)
                    .content(content)
                    .memberId(newMember.getId())
                    .categoryId(childCategory.getId())
                    .build();
            postService.create(formDto);
        }

        List<PostDto> posts = postService.getList(PageRequest.of(0, 10)).getContent();

        assertThat(posts.get(9).getTitle()).isEqualTo("title9");
        assertThat(posts.get(9).getContent()).isEqualTo("content9");
    }

    @Test
    public void getListByCategory_Success() throws Exception {
        String title="title";
        String content="content";

        PostFormDto formDto = PostFormDto.builder()
                .title(title)
                .content(content)
                .memberId(newMember.getId())
                .categoryId(childCategory.getId())
                .build();
        postService.create(formDto);

        assertThat(postService.getListByCategory(childCategory.getId(), PageRequest.of(0, 10)).getContent().get(0).getTitle()).isEqualTo(title);
        assertThat(postService.getListByCategory(childCategory.getId(), PageRequest.of(0, 10)).getContent().get(0).getContent()).isEqualTo(content);
    }

    @Test
    public void getListByCategory_Failure_InvalidCategoryId() {
        // given
        Long invalidCategoryId = -1L;
        Pageable pageable = PageRequest.of(0, 10);

        // fail: get post list with invalid categoryId.
        assertThatThrownBy(()->{postService.getListByCategory(invalidCategoryId, pageable);})
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining(String.valueOf(invalidCategoryId));
    }
    @Test
    public void searchListByKeyword_Success() throws Exception {
        for(int i=0; i<10; i++) {
            String title="title" + i;
            String content="content" + i;

            PostFormDto formDto = PostFormDto.builder()
                    .title(title)
                    .content(content)
                    .memberId(newMember.getId())
                    .categoryId(childCategory.getId())
                    .build();
            postService.create(formDto);
        }

        assertThat(postService.searchListByKeyword("title4", PageRequest.of(0, 10)).getContent().get(0).getTitle()).isEqualTo("title4");
        assertThat(postService.searchListByKeyword("content4", PageRequest.of(0, 10)).getContent().get(0).getContent()).isEqualTo("content4");
    }

    @Test
    public void react_Success() throws Exception {
        PostFormDto formDto = PostFormDto.builder()
                .title("title")
                .content("content")
                .memberId(newMember.getId())
                .categoryId(childCategory.getId())
                .build();

        PostDto created = postService.create(formDto);

        postService.react(newMember.getId(), created.getId(), true);
        assertThat(postService.get(created.getId()).getLikeCount()).isEqualTo(1);
        assertThat(postService.get(created.getId()).getDislikeCount()).isEqualTo(0);

        postService.react(newMember.getId(), created.getId(), false);
        assertThat(postService.get(created.getId()).getLikeCount()).isEqualTo(0);
        assertThat(postService.get(created.getId()).getDislikeCount()).isEqualTo(1);
    }

    @Test
    public void react_Failure() throws Exception {
        PostFormDto formDto = PostFormDto.builder()
                .title("title")
                .content("content")
                .memberId(newMember.getId())
                .categoryId(childCategory.getId())
                .build();

        PostDto created = postService.create(formDto);

        // fail: react with invalid memberId.
        assertThatThrownBy(()->{postService.react(-1L, created.getId(), true);})
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("-1");

        // fail: react with invalid postId.
        assertThatThrownBy(()->{postService.react(newMember.getId(), -1L, true);})
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("-1");
    }
}