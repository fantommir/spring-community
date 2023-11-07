package com.JKS.community.service;

import com.JKS.community.dto.*;
import com.JKS.community.entity.Category;
import com.JKS.community.entity.Comment;
import com.JKS.community.entity.Member;
import com.JKS.community.entity.Post;
import com.JKS.community.exception.CommentNotFoundException;
import com.JKS.community.exception.PostNotFoundException;
import com.JKS.community.exception.member.MemberNotFoundException;
import com.JKS.community.repository.CategoryRepository;
import com.JKS.community.repository.CommentRepository;
import com.JKS.community.repository.MemberRepository;
import com.JKS.community.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentServiceImpl commentService;

    private MemberFormDto memberFormDto;
    private MemberFormDto anotherMemberFormDto;
    private CategoryFormDto categoryFormDto;
    private CategoryFormDto tabFormDto;
    private PostFormDto postFormDto;
    private CommentFormDto parentCommentFormDto;
    private CommentFormDto childCommentFormDto;

    private Member member;
    private Member anotherMember;
    private Category category;
    private Category tab;
    private Post post;
    private Comment parentComment;
    private Comment childComment;

    @BeforeEach
    void setup() {
        // member setup
        memberFormDto = MemberFormDto.builder()
                .name("member")
                .email("member email")
                .password("password0")
                .confirm_password("password0")
                .build();
        member = Member.of(memberFormDto.getEmail(), memberFormDto.getName(), memberFormDto.getPassword());
        ReflectionTestUtils.setField(member, "id", 1L);

        anotherMemberFormDto = MemberFormDto.builder()
                .name("another name")
                .email("another email")
                .password("password1")
                .confirm_password("password1")
                .build();
        anotherMember = Member.of(anotherMemberFormDto.getEmail(), anotherMemberFormDto.getName(), anotherMemberFormDto.getPassword());
        ReflectionTestUtils.setField(anotherMember, "id", 2L);

        // category setup
        categoryFormDto = CategoryFormDto.builder()
                .name("parent")
                .parentId(null)
                .enabled(true)
                .build();
        category = Category.of(categoryFormDto.getName(), null, categoryFormDto.getEnabled());
        ReflectionTestUtils.setField(category, "id", 1L);

        tabFormDto = CategoryFormDto.builder()
                .name("child")
                .parentId(category.getId())
                .enabled(true)
                .build();
        tab = Category.of(tabFormDto.getName(), category, tabFormDto.getEnabled());
        ReflectionTestUtils.setField(tab, "id", 2L);

        // post setup
        postFormDto = PostFormDto.builder()
                .title("title")
                .content("content")
                .memberId(member.getId())
                .categoryId(tab.getId())
                .build();
        post = Post.of(postFormDto.getTitle(), postFormDto.getContent(), member, tab);
        ReflectionTestUtils.setField(post, "id", 1L);

        // comment setup
        parentCommentFormDto = CommentFormDto.builder()
                .content("parent comment")
                .memberId(member.getId())
                .postId(post.getId())
                .parentId(null)
                .build();
        parentComment = Comment.of(null, 0, post, member, parentCommentFormDto.getContent());
        ReflectionTestUtils.setField(parentComment, "id", 1L);

        childCommentFormDto = CommentFormDto.builder()
                .content("child comment")
                .memberId(anotherMember.getId())
                .postId(post.getId())
                .parentId(parentComment.getId())
                .build();
        childComment = Comment.of(parentComment, 1, post, member, childCommentFormDto.getContent());
        ReflectionTestUtils.setField(childComment, "id", 2L);
    }

    @Test
    @DisplayName("댓글, 대댓글 생성 - 성공")
    public void create_success() {
        // given
        when(postRepository.findById(1L)).thenReturn(Optional.of(post)); // 게시글
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member)); // 회원 1
        when(commentRepository.findById(1L)).thenReturn(Optional.of(parentComment)); // 댓글
        when(memberRepository.findById(2L)).thenReturn(Optional.of(anotherMember)); // 회원 2
        // 대댓글의 경우 부모 댓글을 조회하므로 자식 댓글은 조회하지 않음
//        when(commentRepository.findById(2L)).thenReturn(Optional.of(childComment)); // 댓글에 달린 대댓글

        // when
        CommentDto parentCommentDto = commentService.create(parentCommentFormDto);
        CommentDto childCommentDto = commentService.create(childCommentFormDto);

        // then
        assertThat(parentCommentDto).isNotNull();
        assertThat(parentCommentDto.getContent()).isEqualTo(parentCommentFormDto.getContent());
        assertThat(parentCommentDto.getMemberId()).isEqualTo(member.getId());
        assertThat(parentCommentDto.getPostId()).isEqualTo(post.getId());
        assertThat(parentCommentDto.getParentId()).isEqualTo(parentCommentFormDto.getParentId());

        assertThat(childCommentDto).isNotNull();
        assertThat(childCommentDto.getContent()).isEqualTo(childCommentFormDto.getContent());
        assertThat(childCommentDto.getMemberId()).isEqualTo(anotherMember.getId());
        assertThat(childCommentDto.getPostId()).isEqualTo(post.getId());
        assertThat(childCommentDto.getParentId()).isEqualTo(childCommentFormDto.getParentId());

        verify(postRepository, times(2)).findById(anyLong());
        verify(memberRepository, times(2)).findById(anyLong());
        verify(commentRepository, times(2)).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글, 대댓글 생성 - 실패: 부모 댓글이 없는 경우")
    public void create_postNotFound() {
        // given
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty()); // 게시글

        // when
        // then
        assertThatThrownBy(() -> commentService.create(parentCommentFormDto))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("댓글, 대댓글 생성 - 실패: 회원이 없는 경우")
    public void create_memberNotFound() {
        // given
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post)); // 게시글
        when(memberRepository.findById(member.getId())).thenReturn(Optional.empty()); // 회원 1

        // when
        // then
        assertThatThrownBy(() -> commentService.create(parentCommentFormDto))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("댓글, 대댓글 생성 - 실패: 부모 댓글이 잘못된 경우")
    public void create_parentCommentNotFound() {
        // given
        parentCommentFormDto.setParentId(-1L); // 잘못된 부모 댓글 Id
        ReflectionTestUtils.setField(parentComment, "id", -1L);

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post)); // 게시글
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member)); // 회원 1
        when(memberRepository.findById(anotherMember.getId())).thenReturn(Optional.of(anotherMember)); // 회원 2
        when(commentRepository.findById(-1L)).thenReturn(Optional.of(parentComment)); // 댓글

        // when
        commentService.create(parentCommentFormDto);

        // then
        assertThatThrownBy(() -> commentService.create(childCommentFormDto))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("댓글 수정 - 성공")
    public void update_success() {
        // given
        when(commentRepository.findById(parentComment.getId())).thenReturn(Optional.of(parentComment));
        String updatedContent = "updated content";

        // when
        CommentDto updatedCommentDto = commentService.update(parentComment.getId(), updatedContent);

        // then
        assertThat(updatedCommentDto).isNotNull();
        assertThat(updatedCommentDto.getContent()).isEqualTo(updatedContent);
        assertThat(updatedCommentDto.getMemberId()).isEqualTo(member.getId());
        assertThat(updatedCommentDto.getPostId()).isEqualTo(post.getId());
        assertThat(updatedCommentDto.getParentId()).isEqualTo(parentCommentFormDto.getParentId());
    }

    @Test
    @DisplayName("댓글 수정 - 실패: 댓글이 없는 경우")
    public void update_commentNotFound() {
        // given
        when(commentRepository.findById(-1L)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> commentService.update(-1L, "updated content"))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("댓글 삭제 - 성공")
    public void delete_success() {
        // given
        when(commentRepository.findById(parentComment.getId())).thenReturn(Optional.of(parentComment));

        // when
        commentService.delete(parentComment.getId());

        // then
        verify(commentRepository, times(1)).delete(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 삭제 - 실패: 댓글이 없는 경우")
    public void delete_commentNotFound() {
        // given
        when(commentRepository.findById(-1L)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> commentService.delete(-1L))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("게시글로 댓글 조회")
    public void getListByPost() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Comment> comments = Collections.singletonList(parentComment);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

        // 스텁 설정
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(commentRepository.findAllByPostId(post.getId(), pageable)).thenReturn(commentPage);

        // when
        Page<CommentDto> commentDtoPage = commentService.getListByPost(post.getId(), pageable);

        // then
        assertThat(commentDtoPage).isNotNull();
        assertThat(commentDtoPage.getTotalElements()).isEqualTo(comments.size());
        assertThat(commentDtoPage.getContent().get(0)).isInstanceOf(CommentDto.class);

        // 검증
        verify(postRepository).findById(post.getId());
        verify(commentRepository).findAllByPostId(post.getId(), pageable);
    }

}