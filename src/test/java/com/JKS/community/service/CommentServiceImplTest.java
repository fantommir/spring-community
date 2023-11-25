package com.JKS.community.service;

import com.JKS.community.dto.*;
import com.JKS.community.entity.*;
import com.JKS.community.exception.CommentNotFoundException;
import com.JKS.community.exception.PostNotFoundException;
import com.JKS.community.exception.member.MemberNotFoundException;
import com.JKS.community.repository.CommentRepository;
import com.JKS.community.repository.MemberRepository;
import com.JKS.community.repository.PostRepository;
import com.JKS.community.repository.ReactionRepository;
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

    @Mock private MemberRepository memberRepository;
    @Mock private PostRepository postRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private ReactionRepository reactionRepository;
    @InjectMocks private CommentServiceImpl commentService;

    private CommentFormDto parentCommentFormDto;
    private CommentFormDto childCommentFormDto;

    private Member member;
    private Member anotherMember;
    private Post post;
    private Comment parentComment;
    private Comment childComment;
    private Reaction likeReaction;
    private Reaction dislikeReaction;

    @BeforeEach
    void setup() {
        // member setup
        MemberFormDto memberFormDto = MemberFormDto.builder()
                .name("member")
                .email("member email")
                .password("password0")
                .confirm_password("password0")
                .build();
        member = Member.of(memberFormDto.getEmail(), memberFormDto.getName(), memberFormDto.getPassword());
        ReflectionTestUtils.setField(member, "id", 1L);

        MemberFormDto anotherMemberFormDto = MemberFormDto.builder()
                .name("another name")
                .email("another email")
                .password("password1")
                .confirm_password("password1")
                .build();
        anotherMember = Member.of(anotherMemberFormDto.getEmail(), anotherMemberFormDto.getName(), anotherMemberFormDto.getPassword());
        ReflectionTestUtils.setField(anotherMember, "id", 2L);

        // category setup
        CategoryFormDto categoryFormDto = CategoryFormDto.builder()
                .name("parent")
                .parentId(null)
                .enabled(true)
                .build();
        Category category = Category.of(categoryFormDto.getName(), null, categoryFormDto.getEnabled());
        ReflectionTestUtils.setField(category, "id", 1L);

        CategoryFormDto tabFormDto = CategoryFormDto.builder()
                .name("child")
                .parentId(category.getId())
                .enabled(true)
                .build();
        Category tab = Category.of(tabFormDto.getName(), category, tabFormDto.getEnabled());
        ReflectionTestUtils.setField(tab, "id", 2L);

        // post setup
        PostFormDto postFormDto = PostFormDto.builder()
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

        // reaction setup
        likeReaction = Reaction.of(member, parentComment, true);
        ReflectionTestUtils.setField(likeReaction, "id", 1L);
        dislikeReaction = Reaction.of(anotherMember, childComment, false);
        ReflectionTestUtils.setField(dislikeReaction, "id", 2L);
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

        verify(commentRepository, times(1)).findById(parentComment.getId());
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
    @DisplayName("게시글로 댓글 조회 - 성공")
    public void getListByPost() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Comment> comments = Collections.singletonList(parentComment);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(commentRepository.findAllByPostId(post.getId(), pageable)).thenReturn(commentPage);

        // when
        Page<CommentDto> commentDtoPage = commentService.getListByPost(post.getId(), pageable);

        // then
        assertThat(commentDtoPage).isNotNull();
        assertThat(commentDtoPage.getTotalElements()).isEqualTo(comments.size());
        assertThat(commentDtoPage.getContent().get(0)).isInstanceOf(CommentDto.class);

        verify(postRepository, times(1)).findById(post.getId());
        verify(commentRepository, times(1)).findAllByPostId(post.getId(), pageable);
    }

    @Test
    @DisplayName("게시글로 댓글 조회 - 실패: 게시글이 없는 경우")
    public void getListByPost_postNotFound() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> commentService.getListByPost(post.getId(), pageable))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("회원으로 댓글 조회")
    public void getListByMember() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Comment> comments = Collections.singletonList(parentComment);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(commentRepository.findAllByMemberId(member.getId(), pageable)).thenReturn(commentPage);

        // when
        Page<CommentDto> commentDtoPage = commentService.getListByMember(member.getId(), pageable);

        // then
        assertThat(commentDtoPage).isNotNull();
        assertThat(commentDtoPage.getTotalElements()).isEqualTo(comments.size());
        assertThat(commentDtoPage.getContent().get(0)).isInstanceOf(CommentDto.class);

        verify(memberRepository, times(1)).findById(member.getId());
        verify(commentRepository, times(1)).findAllByMemberId(member.getId(), pageable);
    }

    @Test
    @DisplayName("회원으로 댓글 조회 - 실패: 회원이 없는 경우")
    public void getListByMember_memberNotFound() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> commentService.getListByMember(member.getId(), pageable))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("게시글에서 루트 댓글 조회")
    public void getRootCommentsByPost() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Comment> comments = Collections.singletonList(parentComment);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

        when(postRepository.existsById(post.getId())).thenReturn(true);
        when(commentRepository.findAllByPostIdAndLevel(post.getId(), 0, pageable)).thenReturn(commentPage);

        // when
        Page<CommentDto> commentDtoPage = commentService.getRootCommentsByPost(post.getId(), pageable);

        // then
        assertThat(commentDtoPage).isNotNull();
        assertThat(commentDtoPage.getTotalElements()).isEqualTo(comments.size());
        assertThat(commentDtoPage.getContent().get(0)).isInstanceOf(CommentDto.class);

        verify(postRepository, times(1)).existsById(post.getId());
        verify(commentRepository, times(1)).findAllByPostIdAndLevel(post.getId(), 0, pageable);
    }

    @Test
    @DisplayName("게시글에서 루트 댓글 조회 - 실패: 게시글이 없는 경우")
    public void getRootCommentsByPost_postNotFound() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        when(postRepository.existsById(post.getId())).thenReturn(false);

        // when
        // then
        assertThatThrownBy(() -> commentService.getRootCommentsByPost(post.getId(), pageable))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("부모 댓글로 대댓글 조회 - 성공")
    public void getCommentByParent() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Comment> comments = Collections.singletonList(childComment);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

        when(commentRepository.findById(parentComment.getId())).thenReturn(Optional.of(parentComment));
        when(commentRepository.findAllByParentId(parentComment.getId(), pageable)).thenReturn(commentPage);

        // when
        Page<CommentDto> commentDtoPage = commentService.getCommentByParent(parentComment.getId(), pageable);

        // then
        assertThat(commentDtoPage).isNotNull();
        assertThat(commentDtoPage.getTotalElements()).isEqualTo(comments.size());
        assertThat(commentDtoPage.getContent().get(0)).isInstanceOf(CommentDto.class);

        verify(commentRepository, times(1)).findById(parentComment.getId());
        verify(commentRepository, times(1)).findAllByParentId(parentComment.getId(), pageable);
    }

    @Test
    @DisplayName("부모 댓글로 대댓글 조회 - 실패: 부모 댓글이 없는 경우")
    public void getCommentByParent_commentNotFound() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        when(commentRepository.findById(parentComment.getId())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> commentService.getCommentByParent(parentComment.getId(), pageable))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("반응(좋아요 2번 눌러서 취소) - 성공")
    public void react_cancelReaction() {
        // given
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(commentRepository.findById(parentComment.getId())).thenReturn(Optional.of(parentComment));
        when(reactionRepository.findByMemberIdAndCommentId(member.getId(), parentComment.getId())).thenReturn(Optional.empty()); // 첫 반응

        // when
        CommentDto commentDto1 = commentService.react(member.getId(), parentComment.getId(), true);
        when(reactionRepository.findByMemberIdAndCommentId(member.getId(), parentComment.getId())).thenReturn(Optional.of(likeReaction)); // 동일한 반응 눌러서 취소
        CommentDto commentDto2 = commentService.react(member.getId(), parentComment.getId(), true);

        // then
        // 첫 번째 호출 후에는 좋아요 수가 1 증가했어야 함
        assertThat(commentDto1).isNotNull();
        assertThat(commentDto1.getLikeCount()).isEqualTo(1);
        assertThat(commentDto1.getDislikeCount()).isEqualTo(0);

        // 두 번째 호출 후에는 좋아요 수가 1 감소했어야 함
        assertThat(commentDto2).isNotNull();
        assertThat(commentDto2.getLikeCount()).isEqualTo(0);
        assertThat(commentDto2.getDislikeCount()).isEqualTo(0);

        // 검증
        verify(memberRepository, times(2)).findById(anyLong());
        verify(commentRepository, times(2)).findById(anyLong());
        verify(reactionRepository, times(2)).findByMemberIdAndCommentId(anyLong(), anyLong());
        verify(reactionRepository).delete(any(Reaction.class));
    }
    
    @Test
    @DisplayName("반응(좋아요 -> 싫어요) - 성공")
    public void react_changeReaction() {
        // given
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(commentRepository.findById(parentComment.getId())).thenReturn(Optional.of(parentComment));
        when(reactionRepository.findByMemberIdAndCommentId(member.getId(), parentComment.getId())).thenReturn(Optional.empty()); // 첫 반응
        
        // when
        CommentDto commentDto1 = commentService.react(member.getId(), parentComment.getId(), true);
        when(reactionRepository.findByMemberIdAndCommentId(member.getId(), parentComment.getId())).thenReturn(Optional.of(likeReaction)); // 다른 반응 눌러서 변경
        CommentDto commentDto2 = commentService.react(member.getId(), parentComment.getId(), false);

        // then
        // 첫 번째 호출 후에는 좋아요 수가 1 증가했어야 함
        assertThat(commentDto1).isNotNull();
        assertThat(commentDto1.getLikeCount()).isEqualTo(1);
        assertThat(commentDto1.getDislikeCount()).isEqualTo(0);

        // 두 번째 호출 후에는 좋아요 수가 1 감소했어야 함
        assertThat(commentDto2).isNotNull();
        assertThat(commentDto2.getLikeCount()).isEqualTo(0);
        assertThat(commentDto2.getDislikeCount()).isEqualTo(1);

        // 검증
        verify(memberRepository, times(2)).findById(anyLong());
        verify(commentRepository, times(2)).findById(anyLong());
        verify(reactionRepository, times(2)).findByMemberIdAndCommentId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("반응 - 실패: 회원이 없는 경우")
    public void react_memberNotFound() {
        // given
        when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> commentService.react(member.getId(), parentComment.getId(), true))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("반응 - 실패: 댓글이 없는 경우")
    public void react_commentNotFound() {
        // given
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(commentRepository.findById(parentComment.getId())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> commentService.react(member.getId(), parentComment.getId(), true))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("회원 댓글 수 조회 - 성공")
    public void countCommentsByMember() {
        // given
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(memberRepository.countCommentsByMember(member)).thenReturn(4L);

        // when
        Long count = commentService.countCommentsByMember(member.getId());

        // then
        assertThat(count).isEqualTo(4L);

        verify(memberRepository, times(1)).findById(member.getId());
        verify(memberRepository, times(1)).countCommentsByMember(member);
    }

    @Test
    @DisplayName("회원 댓글 수 조회 - 실패: 회원이 없는 경우")
    public void countCommentsByMember_memberNotFound() {
        // given
        when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> commentService.countCommentsByMember(member.getId()))
                .isInstanceOf(MemberNotFoundException.class);
    }
}