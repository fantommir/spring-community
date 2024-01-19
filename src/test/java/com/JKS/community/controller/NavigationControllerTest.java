package com.JKS.community.controller;

import com.JKS.community.dto.CategoryDto;
import com.JKS.community.dto.MemberDto;
import com.JKS.community.dto.PostDto;
import com.JKS.community.entity.Member;
import com.JKS.community.security.UserDetailsService;
import com.JKS.community.service.CategoryService;
import com.JKS.community.service.CommentService;
import com.JKS.community.service.MemberService;
import com.JKS.community.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NavigationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private PostService postService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("/ GET")
    public void home() throws Exception {
        // given
        List<CategoryDto> categoryList = new ArrayList<>();
        CategoryDto category = new CategoryDto();
        category.setId(1L);
        category.setName("Test Category");
        categoryList.add(category);

        when(categoryService.getListByDepth(0)).thenReturn(categoryList);

        // when & then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("categoryList", categoryList))
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("/register GET")
    public void register() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("memberFormDto"))
                .andExpect(view().name("register"));
    }

    @Test
    @DisplayName("/login GET")
    public void login() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("/login POST")
    public void processLogin() throws Exception {
        // given
        String email = "valid_email@email.com";
        String name = "valid_name";
        String validPassword = "valid_password";

        Member member = Member.of(email, name, validPassword);

        // when
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(member);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // then
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("email", email)
                        .param("password", validPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }


    @Test
    @DisplayName("/info GET - 로그인하지 않은 경우")
    @WithAnonymousUser
    public void info_anonymous() throws Exception {
        mockMvc.perform(get("/info"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("/info/{memberId} GET - 로그인한 경우")
    public void info_user() throws Exception {
        //given
        String email = "email@email.com";
        String name = "name";
        String validPassword = "vaildPW0";

        Member member = Member.of(email, name, validPassword);

        when(userDetailsService.loadUserByUsername(email)).thenReturn(member);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        MemberDto memberDto = new MemberDto(member);
        when(memberService.get(1L)).thenReturn(memberDto);

        when(postService.countPostsByMember(1L)).thenReturn(3L);
        when(commentService.countCommentsByMember(1L)).thenReturn(12L);

        //when&then
        mockMvc.perform(get("/info/1").with(user(member)))
                .andExpect(status().isOk())
                .andExpect(view().name("info"))
                .andExpect(model().attribute("countPosts", 3L))
                .andExpect(model().attribute("countComments", 12L))
                .andExpect(model().attribute("memberDto", memberDto));
    }



    @Test
    @DisplayName("/info/{memberId}/edit GET")
    public void editInfo() throws Exception {
        // given
        MemberDto memberDto = new MemberDto();
        memberDto.setId(1L);
        memberDto.setEmail("email@email.com");
        memberDto.setName("name");
        when(memberService.get(1L)).thenReturn(memberDto);

        // when & then
        mockMvc.perform(get("/info/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("info-form"))
                .andExpect(model().attribute("memberDto", memberDto));
    }

    @Test
    @DisplayName("/category/{categoryId} GET")
    public void postsByCategory() throws Exception {
        // given
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");
        when(categoryService.get(1L)).thenReturn(categoryDto);

        List<PostDto> postDtos = getPostDtos(30);

        // Create a Page object from the List
        Pageable pageable = PageRequest.of(0, 20);
        Page<PostDto> postDtoPage = new PageImpl<>(postDtos, pageable, postDtos.size());
        when(postService.getListByParentCategory(1L, pageable)).thenReturn(postDtoPage);

        // when & then
        mockMvc.perform(get("/category/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("category", "postList"))
                .andExpect(model().attribute("category", categoryDto))
                .andExpect(model().attribute("postList", postDtoPage))
                .andExpect(view().name("posts-by-category"));
    }

    @Test
    @DisplayName("/post/member GET - 로그인하지 않은 경우")
    @WithAnonymousUser
    public void postsByMember_anonymous() throws Exception {
        mockMvc.perform(get("/post/member"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("/post/member GET - 로그인한 경우")
    public void postsByMember_user() throws Exception {
        // given
        String email = "email@email.com";
        String name = "name";
        String validPassword = "valid_password";

        Member member = Member.of(email, name, validPassword);

        when(userDetailsService.loadUserByUsername(email)).thenReturn(member);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        MemberDto memberDto = new MemberDto(member);
        when(memberService.get(1L)).thenReturn(memberDto);

        List<PostDto> postDtos = getPostDtos(30);
        List<PostDto> postDtosByMemberId = postDtos.stream()
                .filter(postDto -> postDto.getMemberId().equals(1L))
                .toList();

        Pageable pageable = PageRequest.of(0, 20);
        Page<PostDto> postDtoPage = new PageImpl<>(postDtosByMemberId, pageable, postDtosByMemberId.size());
        when(postService.getListByMember(1L, pageable)).thenReturn(postDtoPage);

        // when & then
        mockMvc.perform(get("/post/member/1").with(user(member)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("memberDto", "postList"))
                .andExpect(model().attribute("memberDto", memberDto))
                .andExpect(model().attribute("postList", postDtoPage))
                .andExpect(view().name("posts-by-member"));
    }


    @Test
    @DisplayName("/post/{postId} GET - 로그인한 경우")
    public void postView() throws Exception {
        // given
        String email = "email@email.com";
        String name = "name";
        String validPassword = "valid_password";

        Member member = Member.of(email, name, validPassword);

        when(userDetailsService.loadUserByUsername(email)).thenReturn(member);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        PostDto postDto = getPostDtos(1).get(0);
        postDto.setMemberId(member.getId());
        when(postService.get(postDto.getId())).thenReturn(postDto);

        // when & then
        mockMvc.perform(get("/post/"+postDto.getId()).with(user(email)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("memberId", postDto.getMemberId()))
                .andExpect(model().attribute("postDto", postDto))
                .andExpect(view().name("post-view"));

        verify(postService, times(1)).increaseViewCount(postDto.getId());
    }

    @Test
    @DisplayName("/{categoryId}/create GET - 로그인한 경우")
    public void createPost_user() throws Exception {
        // given
        String email = "email@email.com";
        String name = "name";
        String validPassword = "valid_password";

        Member member = Member.of(email, name, validPassword);

        when(userDetailsService.loadUserByUsername(email)).thenReturn(member);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");
        when(categoryService.get(1L)).thenReturn(categoryDto);

        // when & then
        mockMvc.perform(get("/1/create").with(user(email)))
                .andExpect(status().isOk())
                .andExpect(view().name("post-form"))
                .andExpect(model().attribute("postDto", instanceOf(PostDto.class)))
                .andExpect(model().attribute("category", categoryDto));
    }

    @Test
    @DisplayName("/post/{postId}/edit GET - 로그인한 경우")
    public void editPost_user() throws Exception {
        // given
        String email = "email@email.com";
        String name = "name";
        String validPassword = "valid_password";

        Member member = Member.of(email, name, validPassword);

        when(userDetailsService.loadUserByUsername(email)).thenReturn(member);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");

        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setParentCategoryId(1L);
        when(postService.get(1L)).thenReturn(postDto);
        when(categoryService.get(postDto.getParentCategoryId())).thenReturn(categoryDto);

        // when & then
        mockMvc.perform(get("/post/1/edit").with(user(email)))
                .andExpect(status().isOk())
                .andExpect(view().name("post-form"))
                .andExpect(model().attribute("postDto", postDto))
                .andExpect(model().attribute("category", categoryDto));
    }




    private List<PostDto> getPostDtos(int n) {
        List<PostDto> postDtos = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            PostDto postDto = new PostDto();
            postDto.setId((long) i);
            postDto.setTitle("Test Post " + i);
            postDto.setContent("Test Content " + i);
            postDto.setViewCount(i);
            postDto.setLikeCount(i);
            postDto.setDislikeCount(n-i);
            postDto.setEnabled(true);
            postDto.setMemberId((long) i);
            postDto.setMemberName("Test Member "+i);
            postDto.setCategoryId((long) i);
            postDto.setCategoryName("Test Category "+i);
            postDto.setParentCategoryId((long) i);
            postDto.setParentCategoryName("Test Parent Category "+i);
            postDtos.add(postDto);
        }
        return postDtos;
    }

}