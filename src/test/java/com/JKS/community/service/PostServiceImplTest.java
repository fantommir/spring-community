package com.JKS.community.service;

import com.JKS.community.dto.*;
import com.JKS.community.entity.*;

import com.JKS.community.repository.*;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock private MemberRepository memberRepository;
    @Mock private PostRepository postRepository;
    @Mock private ReactionRepository reactionRepository;
    @Mock private CategoryRepository categoryRepository;
    @InjectMocks private PostServiceImpl postService;

    private PostFormDto post1FormDto;
    private PostFormDto post2FormDto;

    private Member member;
    private Member anotherMember;
    private Category category;
    private Category tab1;
    private Category tab2;
    private Post post1;
    private Post post2;
    private Reaction likeReaction;

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
        category = Category.of(categoryFormDto.getName(), null, categoryFormDto.getEnabled());
        ReflectionTestUtils.setField(category, "id", 1L);

        CategoryFormDto tab1FormDto = CategoryFormDto.builder()
                .name("tab1")
                .parentId(category.getId())
                .enabled(true)
                .build();
        tab1 = Category.of(tab1FormDto.getName(), category, tab1FormDto.getEnabled());
        ReflectionTestUtils.setField(tab1, "id", 2L);

        CategoryFormDto tab2FormDto = CategoryFormDto.builder()
                .name("tab2")
                .parentId(category.getId())
                .enabled(true)
                .build();
        tab2 = Category.of(tab2FormDto.getName(), category, tab2FormDto.getEnabled());
        ReflectionTestUtils.setField(tab2, "id", 3L);


        // post setup
        post1FormDto = PostFormDto.builder()
                .title("member - Post1 title")
                .content("Post1 content")
                .memberId(member.getId())
                .categoryId(tab1.getId())
                .build();
        post1 = Post.of(post1FormDto.getTitle(), post1FormDto.getContent(), member, tab1);
        ReflectionTestUtils.setField(post1, "id", 1L);

        post2FormDto = PostFormDto.builder()
                .title("anotherMember - Post2 title")
                .content("Post2 content")
                .memberId(anotherMember.getId())
                .categoryId(tab1.getId())
                .build();
        post2 = Post.of(post2FormDto.getTitle(), post2FormDto.getContent(), anotherMember, tab1);
        ReflectionTestUtils.setField(post2, "id", 2L);


        // reaction setup
        likeReaction = Reaction.of(member, post1, true);
        ReflectionTestUtils.setField(likeReaction, "id", 1L);
    }

    @Test
    @DisplayName("게시글 생성")
    public void create_success() {
        // given
        when(memberRepository.findById(post1.getMember().getId())).thenReturn(Optional.of(member));
        when(categoryRepository.findById(post1.getCategory().getId())).thenReturn(Optional.of(tab1));

        // when
        PostDto postDto = postService.create(post1FormDto);

        // then
        assertThat(postDto).isNotNull();
        assertThat(postDto.getTitle()).isEqualTo(post1.getTitle());
        assertThat(postDto.getContent()).isEqualTo(post1.getContent());
        assertThat(postDto.getMemberId()).isEqualTo(post1.getMember().getId());
        assertThat(postDto.getCategoryId()).isEqualTo(post1.getCategory().getId());

        verify(memberRepository, times(1)).findById(member.getId());
        verify(categoryRepository, times(1)).findById(post1.getCategory().getId());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 수정")
    public void update() {
        // given
        when(postRepository.findById(post1.getId())).thenReturn(Optional.of(post1));
        when(categoryRepository.findById(post1.getCategory().getId())).thenReturn(Optional.of(tab1));

        PostFormDto updatePostFormDto = PostFormDto.builder()
                .title("update title")
                .content("update content")
                .memberId(member.getId())
                .categoryId(tab1.getId())
                .build();

        // when
        PostDto postDto = postService.update(post1.getId(), updatePostFormDto);

        // then
        assertThat(postDto).isNotNull();
        assertThat(postDto.getTitle()).isEqualTo(updatePostFormDto.getTitle());
        assertThat(postDto.getContent()).isEqualTo(updatePostFormDto.getContent());
        assertThat(postDto.getMemberId()).isEqualTo(updatePostFormDto.getMemberId());
        assertThat(postDto.getCategoryId()).isEqualTo(updatePostFormDto.getCategoryId());

        verify(postRepository, times(1)).findById(post1.getId());
        verify(categoryRepository, times(1)).findById(tab1.getId());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 삭제")
    public void delete() {
        // given
        when(postRepository.findById(post1.getId())).thenReturn(Optional.of(post1));

        // when
        postService.delete(post1.getId());

        // then
        verify(postRepository, times(1)).findById(post1.getId());
        verify(postRepository, times(1)).delete(any(Post.class));
    }

    @Test
    @DisplayName("게시글 조회")
    public void get() {
        // given
        when(postRepository.findById(post1.getId())).thenReturn(Optional.of(post1));

        // when
        PostDto postDto = postService.get(post1.getId());

        // then
        assertThat(postDto).isNotNull();
        assertThat(postDto.getTitle()).isEqualTo(post1.getTitle());
        assertThat(postDto.getContent()).isEqualTo(post1.getContent());
        assertThat(postDto.getMemberId()).isEqualTo(post1.getMember().getId());
        assertThat(postDto.getCategoryId()).isEqualTo(post1.getCategory().getId());

        verify(postRepository, times(1)).findById(post1.getId());
    }

    @Test
    @DisplayName("모든 게시글 목록 조회")
    public void getList() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> posts = List.of(post1, post2);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());
        when(postRepository.findAll(pageable)).thenReturn(postPage);

        // when
        Page<PostDto> postDtoPage = postService.getList(pageable);

        // then
        assertThat(postDtoPage).isNotNull();
        assertThat(postDtoPage.getContent().size()).isEqualTo(2);
        assertThat(postDtoPage.getContent().get(0).getTitle()).isEqualTo(post1.getTitle());
        assertThat(postDtoPage.getContent().get(0).getContent()).isEqualTo(post1.getContent());
        assertThat(postDtoPage.getContent().get(0).getMemberId()).isEqualTo(post1.getMember().getId());
        assertThat(postDtoPage.getContent().get(0).getCategoryId()).isEqualTo(post1.getCategory().getId());

        verify(postRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("카테고리별 게시글 목록 조회")
    public void getListByCategory() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> posts = List.of(post1);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());
        when(categoryRepository.existsById(post1.getCategory().getId())).thenReturn(true);
        when(postRepository.findAllByCategoryId(post1.getCategory().getId(), pageable)).thenReturn(postPage);

        // when
        Page<PostDto> postDtoPage = postService.getListByCategory(post1.getCategory().getId(), pageable);

        // then
        assertThat(postDtoPage).isNotNull();
        assertThat(postDtoPage.getContent().size()).isEqualTo(1);
        assertThat(postDtoPage.getContent().get(0).getTitle()).isEqualTo(post1.getTitle());
        assertThat(postDtoPage.getContent().get(0).getContent()).isEqualTo(post1.getContent());
        assertThat(postDtoPage.getContent().get(0).getMemberId()).isEqualTo(post1.getMember().getId());
        assertThat(postDtoPage.getContent().get(0).getCategoryId()).isEqualTo(post1.getCategory().getId());

        verify(categoryRepository, times(1)).existsById(post1.getCategory().getId());
        verify(postRepository, times(1)).findAllByCategoryId(post1.getCategory().getId(), pageable);
    }

    @Test
    @DisplayName("상위 카테고리별 게시글 목록 조회")
    public void getListByParentCategory() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> posts = List.of(post1, post2);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(postRepository.findAllByCategoryIdIn(List.of(tab1.getId(), tab2.getId()), pageable)).thenReturn(postPage);

        // when
        Page<PostDto> postDtoPage = postService.getListByParentCategory(category.getId(), pageable);

        // then
        assertThat(postDtoPage).isNotNull();
        assertThat(postDtoPage.getContent().size()).isEqualTo(2);
        assertThat(postDtoPage.getContent().get(0).getTitle()).isEqualTo(post1.getTitle());
        assertThat(postDtoPage.getContent().get(0).getContent()).isEqualTo(post1.getContent());
        assertThat(postDtoPage.getContent().get(0).getMemberId()).isEqualTo(post1.getMember().getId());
        assertThat(postDtoPage.getContent().get(0).getCategoryId()).isEqualTo(post1.getCategory().getId());

        verify(categoryRepository, times(1)).findById(category.getId());
        verify(postRepository, times(1)).findAllByCategoryIdIn(List.of(tab1.getId(), tab2.getId()), pageable);
    }

    @Test
    @DisplayName("회원별 게시글 목록 조회")
    public void getListByMember() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> posts = List.of(post1);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());
        when(memberRepository.findById(post1.getMember().getId())).thenReturn(Optional.of(member));
        when(postRepository.findAllByMemberId(post1.getMember().getId(), pageable)).thenReturn(postPage);

        // when
        Page<PostDto> postDtoPage = postService.getListByMember(post1.getMember().getId(), pageable);

        // then
        assertThat(postDtoPage).isNotNull();
        assertThat(postDtoPage.getContent().size()).isEqualTo(1);
        assertThat(postDtoPage.getContent().get(0).getTitle()).isEqualTo(post1.getTitle());
        assertThat(postDtoPage.getContent().get(0).getContent()).isEqualTo(post1.getContent());
        assertThat(postDtoPage.getContent().get(0).getMemberId()).isEqualTo(post1.getMember().getId());
        assertThat(postDtoPage.getContent().get(0).getCategoryId()).isEqualTo(post1.getCategory().getId());

        verify(memberRepository, times(1)).findById(post1.getMember().getId());
        verify(postRepository, times(1)).findAllByMemberId(post1.getMember().getId(), pageable);
    }

    @Test
    @DisplayName("키워드로 게시글 검색")
    public void searchListByKeyword() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> posts = List.of(post1);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());
        when(postRepository.searchByKeyword("post1", pageable)).thenReturn(postPage);

        // when
        Page<PostDto> postDtoPage = postService.searchListByKeyword("post1", pageable);

        // then
        assertThat(postDtoPage).isNotNull();
        assertThat(postDtoPage.getContent().size()).isEqualTo(1);
        assertThat(postDtoPage.getContent().get(0).getTitle().contains("Post1") || postDtoPage.getContent().get(0).getContent().contains("Post1")).isTrue();
        assertThat(postDtoPage.getContent().get(0).getTitle()).isEqualTo(post1.getTitle());
        assertThat(postDtoPage.getContent().get(0).getContent()).isEqualTo(post1.getContent());
        assertThat(postDtoPage.getContent().get(0).getMemberId()).isEqualTo(post1.getMember().getId());
        assertThat(postDtoPage.getContent().get(0).getCategoryId()).isEqualTo(post1.getCategory().getId());

        verify(postRepository, times(1)).searchByKeyword("post1", pageable);
    }

    @Test
    @DisplayName("게시글 반응(토글)")
    public void react() {
        // given
        when(postRepository.findById(post1.getId())).thenReturn(Optional.of(post1));
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(reactionRepository.findByMemberIdAndPostId(member.getId(), post1.getId())).thenReturn(Optional.empty());

        // when
        PostDto postDto1 = postService.react(member.getId(), post1.getId(), true);
        when(reactionRepository.findByMemberIdAndPostId(member.getId(), post1.getId())).thenReturn(Optional.ofNullable(likeReaction));
        PostDto postDto2 = postService.react(member.getId(), post1.getId(), true);

        // then
        // postDto1에서는 좋아요가 1 증가했다가 postDto2에서는 좋아요가 1 감소했는지 확인
        assertThat(postDto1).isNotNull();
        assertThat(postDto1.getLikeCount()).isEqualTo(1);
        assertThat(postDto2).isNotNull();
        assertThat(postDto2.getLikeCount()).isEqualTo(0);

        verify(postRepository, times(2)).findById(post1.getId());
        verify(memberRepository, times(2)).findById(member.getId());
        verify(reactionRepository, times(2)).findByMemberIdAndPostId(member.getId(), post1.getId());
        verify(reactionRepository, times(1)).save(any(Reaction.class));
        verify(reactionRepository, times(1)).delete(any(Reaction.class));
    }

    @Test
    @DisplayName("게시글 조회수 증가")
    public void increaseViewCount() {
        // given
        when(postRepository.findById(post1.getId())).thenReturn(Optional.of(post1));

        // when
        postService.increaseViewCount(post1.getId());

        // then
        assertThat(post1.getViewCount()).isEqualTo(1);

        verify(postRepository, times(1)).findById(post1.getId());
    }

    @Test
    @DisplayName("회원별 게시글 수 조회")
    public void countPostsByMember() {
        // given
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(memberRepository.countPostsByMember(member)).thenReturn(1L);

        // when
        long count = postService.countPostsByMember(member.getId());

        // then
        assertThat(count).isEqualTo(1L);

        verify(memberRepository, times(1)).findById(member.getId());
        verify(memberRepository, times(1)).countPostsByMember(member);
    }

}