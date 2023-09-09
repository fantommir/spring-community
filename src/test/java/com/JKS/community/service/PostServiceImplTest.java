package com.JKS.community.service;

import com.JKS.community.dto.*;
import com.JKS.community.exception.CategoryNotFoundException;
import com.JKS.community.exception.MemberNotFoundException;
import com.JKS.community.exception.PostNotFoundException;

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

    @Autowired private PostService postService;
    @Autowired private MemberService memberService;
    @Autowired private CategoryService categoryService;

    private MemberDto memberDto;
    private CategoryDto categoryDto;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        // create member
        MemberFormDto memberFormDto = MemberFormDto.builder()
                .loginId("loginId")
                .password("password")
                .name("name").build();
        memberDto = memberService.register(memberFormDto);

        // create category
        CategoryFormDto parentCategoryFormDto = CategoryFormDto.builder()
                .name("name").parentId(null).enabled(true).build();
        categoryService.create(parentCategoryFormDto);
        CategoryFormDto childCategoryFormDto = CategoryFormDto.builder()
                .name("name").parentId(parentCategoryFormDto.getParentId()).enabled(true).build();
        categoryDto = categoryService.create(childCategoryFormDto);

        // create post
        PostFormDto formDto = PostFormDto.builder()
                .title("title").content("content").memberId(memberDto.getId())
                .categoryId(categoryDto.getId()).build();
        postDto = postService.create(formDto);
    }

    @Test
    void createPost_Success() {
        // then
        PostDto findPostDto = postService.get(postDto.getId());
        assertThat(findPostDto.getTitle()).isEqualTo(postDto.getTitle());
        assertThat(findPostDto.getContent()).isEqualTo(postDto.getContent());
    }

    @Test
    void createPost_Failure() {
        // given
        Long invalidMemberId=-1L;
        PostFormDto formDto = PostFormDto.builder()
                .title("title").content("content")
                .memberId(invalidMemberId).categoryId(categoryDto.getId()).build();

        // fail: create post with invalid memberId.
        assertThatThrownBy(() -> postService.create(formDto))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining(String.valueOf(invalidMemberId));

        // fail: get post with invalid postId.
        assertThatThrownBy(() -> postService.get(-1L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("-1");
    }

    @Test
    public void update_Success() {
        PostFormDto formDto = PostFormDto.builder()
                .title("title").content("content").memberId(memberDto.getId())
                .categoryId(categoryDto.getId()).build();

        PostDto postDto = postService.create(formDto);

        String updatedTitle = "updated title";
        formDto.setTitle(updatedTitle);
        String updatedContent = "updated content";
        formDto.setContent(updatedContent);
        PostDto updated = postService.update(postDto.getId(), formDto);

        assertThat(updatedTitle).isEqualTo(updated.getTitle());
        assertThat(updatedContent).isEqualTo(updated.getContent());
    }

    @Test
    public void update_Failure() {
        PostFormDto formDto = PostFormDto.builder()
                .title("updated title").content("updated content").memberId(memberDto.getId())
                .categoryId(categoryDto.getId()).build();

        // fail: update post with invalid postId.
        assertThatThrownBy(()-> postService.update(-1L,formDto))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("-1");
    }

    @Test
    public void delete_Success() {
        postService.delete(postDto.getId());

        assertThatThrownBy(()-> postService.get(postDto.getId()))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining(String.valueOf(postDto.getId()));
    }

    @Test
    public void delete_Failure() {
        // fail: delete post with invalid postId.
        assertThatThrownBy(()-> postService.delete(-1L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("-1");
    }

    @Test
    public void get_Success() {
        PostDto findDto = postService.get(postDto.getId());
        assertThat(findDto.getTitle()).isEqualTo("title");
        assertThat(findDto.getContent()).isEqualTo("content");
    }

    @Test
    public void get_Failure() {
        assertThatThrownBy(()-> postService.get(-1L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("-1");
    }

    @Test
    public void getList_Success() {

        for(int i=0; i<11; i++) {
            String title="title" + i;
            String content="content" + i;

            PostFormDto formDto = PostFormDto.builder()
                    .title(title).content(content).memberId(memberDto.getId())
                    .categoryId(categoryDto.getId()).build();
            postService.create(formDto);
        }

        List<PostDto> posts = postService.getList(PageRequest.of(0, 10)).getContent();
        assertThat(posts.size()).isEqualTo(10);
    }

    @Test
    public void getListByCategory_Success() {
        List<PostDto> postDtoList = postService.getListByCategory(categoryDto.getId(), PageRequest.of(0, 10)).getContent();
        assertThat(postDtoList.get(0).getCategoryName()).isEqualTo(categoryDto.getName());
        assertThat(postDtoList.get(0).getContent()).isEqualTo("content");}

    @Test
    public void getListByCategory_Failure_InvalidCategoryId() {
        // given
        Long invalidCategoryId = -1L;
        Pageable pageable = PageRequest.of(0, 10);

        // fail: get post list with invalid categoryId.
        assertThatThrownBy(()-> postService.getListByCategory(invalidCategoryId, pageable))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining(String.valueOf(invalidCategoryId));
    }
    @Test
    public void searchListByKeyword_Success() {
        for(int i=0; i<10; i++) {
            String title="title" + i;
            String content="content" + i;

            PostFormDto formDto = PostFormDto.builder()
                    .title(title).content(content).memberId(memberDto.getId())
                    .categoryId(categoryDto.getId()).build();
            postService.create(formDto);
        }

        String titleKeyword = "title4";
        String contentKeyword = "content4";
        assertThat(postService.searchListByKeyword(titleKeyword, PageRequest.of(0, 10)).getContent().get(0).getTitle()).isEqualTo(titleKeyword);
        assertThat(postService.searchListByKeyword(contentKeyword, PageRequest.of(0, 10)).getContent().get(0).getContent()).isEqualTo(contentKeyword);
    }

    @Test
    public void react_Success() {
        postService.react(memberDto.getId(), postDto.getId(), true);
        assertThat(postService.get(postDto.getId()).getLikeCount()).isEqualTo(1);
        assertThat(postService.get(postDto.getId()).getDislikeCount()).isEqualTo(0);

        postService.react(memberDto.getId(), postDto.getId(), false);
        assertThat(postService.get(postDto.getId()).getLikeCount()).isEqualTo(0);
        assertThat(postService.get(postDto.getId()).getDislikeCount()).isEqualTo(1);

        // cancel reaction
        postService.react(memberDto.getId(), postDto.getId(), false);
        assertThat(postService.get(postDto.getId()).getLikeCount()).isEqualTo(0);
        assertThat(postService.get(postDto.getId()).getDislikeCount()).isEqualTo(0);
    }

    @Test
    public void react_Failure() {
        // fail: react with invalid memberId.
        assertThatThrownBy(()-> postService.react(-1L, postDto.getId(), true))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("-1");

        // fail: react with invalid postId.
        assertThatThrownBy(()-> postService.react(memberDto.getId(), -1L, true))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("-1");
    }
}