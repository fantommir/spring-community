package com.JKS.community.service;

import com.JKS.community.dto.*;
import com.JKS.community.exception.CommentNotFoundException;
import com.JKS.community.exception.MemberNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceImplTest {

    @Autowired private CommentService commentService;
    @Autowired private MemberService memberService;
    @Autowired private PostService postService;
    @Autowired private CategoryService categoryService;

    private MemberDto memberDto;
    private PostDto postDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        // create member
        MemberFormDto memberFormDto = MemberFormDto.builder()
                .email("email@test.com").name("name").password("password123").confirm_password("password123").build();
        memberDto = memberService.register(memberFormDto);

        // create category
        CategoryFormDto parentCategoryFormDto = CategoryFormDto.builder()
                .name("name").parentId(null).enabled(true).build();
        categoryService.create(parentCategoryFormDto);
        CategoryFormDto childCategoryFormDto = CategoryFormDto.builder()
                .name("name").parentId(parentCategoryFormDto.getParentId()).enabled(true).build();
        CategoryDto categoryDto = categoryService.create(childCategoryFormDto);

        // create post
        PostFormDto formDto = PostFormDto.builder()
                .title("title").content("content").memberId(memberDto.getId())
                .categoryId(categoryDto.getId()).build();
        postDto = postService.create(formDto);

        // create comment
        CommentFormDto commentFormDto = CommentFormDto.builder()
                .content("content").postId(postDto.getId()).memberId(memberDto.getId()).build();
        commentDto = commentService.create(commentFormDto);
    }

    @Test
    void createComment_Success() {
        // then
        CommentDto findCommentDto = commentService.get(commentDto.getId());
        assertThat(findCommentDto.getContent()).isEqualTo(commentDto.getContent());
    }

    @Test
    void createComment_Failure() {
        // given
        Long invalidMemberId=-1L;
        CommentFormDto formDto = CommentFormDto.builder()
                .content("content").postId(postDto.getId())
                .memberId(invalidMemberId).build();

        // fail: create comment with invalid memberId.
        assertThatThrownBy(() -> commentService.create(formDto))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining(String.valueOf(invalidMemberId));

        // fail: get comment with invalid commentId.
        assertThatThrownBy(() -> commentService.get(-1L))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("-1");
    }

    @Test
    void createReply_Success() {
        // given
        CommentFormDto formDto = CommentFormDto.builder()
                .content("reply content").postId(postDto.getId())
                .memberId(memberDto.getId()).parentId(commentDto.getId()).build();

        // when
        CommentDto replyDto = commentService.create(formDto);

        // then
        CommentDto findReplyDto = commentService.get(replyDto.getId());
        assertThat(findReplyDto.getContent()).isEqualTo(replyDto.getContent());
        assertThat(findReplyDto.getParentId()).isEqualTo(commentDto.getId());
    }

    @Test
    void createReply_Failure() {
        // given
        Long invalidParentId = -1L;
        CommentFormDto formDto = CommentFormDto.builder()
                .content("reply content").postId(postDto.getId())
                .memberId(memberDto.getId()).parentId(invalidParentId).build();

        // fail: create reply with invalid parentId.
        assertThatThrownBy(() -> commentService.create(formDto))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining(String.valueOf(invalidParentId));
    }


    @Test
    public void update_Success() {
        CommentFormDto formDto = CommentFormDto.builder()
                .content("content").postId(postDto.getId())
                .memberId(memberDto.getId()).build();

        CommentDto commentDto = commentService.create(formDto);

        String updatedContent = "updated content";
        formDto.setContent(updatedContent);
        CommentDto updated = commentService.update(commentDto.getId(), updatedContent);

        assertThat(updatedContent).isEqualTo(updated.getContent());
    }

    @Test
    public void update_Failure() {
        CommentFormDto formDto = CommentFormDto.builder()
                .content("updated content").postId(postDto.getId())
                .memberId(memberDto.getId()).build();

        // fail: update comment with invalid commentId.
        assertThatThrownBy(()-> commentService.update(-1L,formDto.getContent()))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("-1");
    }

    @Test
    public void delete_Success() {
        commentService.delete(commentDto.getId());

        assertThatThrownBy(()-> commentService.get(commentDto.getId()))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining(String.valueOf(commentDto.getId()));
    }

    @Test
    public void delete_Failure() {
        // fail: delete comment with invalid commentId.
        assertThatThrownBy(()-> commentService.delete(-1L))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("-1");
    }

    @Test
    public void get_Success() {
        CommentDto findDto = commentService.get(commentDto.getId());
        assertThat(findDto.getContent()).isEqualTo("content");
    }

    @Test
    public void get_Failure() {
        assertThatThrownBy(()-> commentService.get(-1L))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("-1");
    }

    @Test
    public void getList_Success() {

        for(int i=0; i<11; i++) {
            String content="content" + i;

            CommentFormDto formDto = CommentFormDto.builder()
                    .content(content).postId(postDto.getId())
                    .memberId(memberDto.getId()).build();
            commentService.create(formDto);
        }

        List<CommentDto> comments = commentService.getListByPost(postDto.getId(), PageRequest.of(0, 10)).getContent();
        assertThat(comments.size()).isEqualTo(10);
    }

    @Test
    public void getListByMember_Success() {
        List<CommentDto> commentDtoList = commentService.getListByMember(memberDto.getId(), PageRequest.of(0, 10)).getContent();
        assertThat(commentDtoList.get(0).getMemberName()).isEqualTo(memberDto.getName());
        assertThat(commentDtoList.get(0).getContent()).isEqualTo("content");
    }

    @Test
    public void react_Success() {
        commentService.react(memberDto.getId(), commentDto.getId(), true);
        assertThat(commentService.get(commentDto.getId()).getLikeCount()).isEqualTo(1);
        assertThat(commentService.get(commentDto.getId()).getDislikeCount()).isEqualTo(0);

        commentService.react(memberDto.getId(), commentDto.getId(), false);
        assertThat(commentService.get(commentDto.getId()).getLikeCount()).isEqualTo(0);
        assertThat(commentService.get(commentDto.getId()).getDislikeCount()).isEqualTo(1);

        commentService.react(memberDto.getId(), commentDto.getId(), false);
        assertThat(commentService.get(commentDto.getId()).getLikeCount()).isEqualTo(0);
        assertThat(commentService.get(commentDto.getId()).getDislikeCount()).isEqualTo(0);
    }

    @Test
    public void react_Failure() {
        // fail: react with invalid memberId.
        assertThatThrownBy(()-> commentService.react(-1L, commentDto.getId(), true))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("-1");

        // fail: react with invalid commentId.
        assertThatThrownBy(()-> commentService.react(memberDto.getId(), -1L, true))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("-1");
    }
}

