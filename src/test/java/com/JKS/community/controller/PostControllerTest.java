package com.JKS.community.controller;

import com.JKS.community.dto.PageRequestDto;
import com.JKS.community.dto.PostDto;
import com.JKS.community.dto.PostFormDto;
import com.JKS.community.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(PostController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Test
    public void create() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setTitle("Test Title");
        postDto.setContent("Test Content");

        when(postService.create(any(PostFormDto.class))).thenReturn(postDto);

        PostFormDto formDto = new PostFormDto();
        formDto.setTitle("Test Title");
        formDto.setContent("Test Content");
        formDto.setMemberId(1L);
        formDto.setCategoryId(1L);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(formDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test Title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("Test Content"))
                .andDo(print());
    }

    @Test
    public void update() throws Exception {
        Long postId = 1L;
        PostDto postDto = new PostDto();
        postDto.setTitle("Updated Title");
        postDto.setContent("Updated Content");

        when(postService.update(eq(postId), any(PostFormDto.class))).thenReturn(postDto);

        PostFormDto formDto = new PostFormDto();
        formDto.setTitle("Updated Title");
        formDto.setContent("Updated Content");
        formDto.setMemberId(1L);
        formDto.setCategoryId(1L);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(formDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated Title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("Updated Content"))
                .andDo(print());
    }


    @Test
    public void delete() throws Exception {
        Long postId = 1L;

        doNothing().when(postService).delete(postId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/" + postId)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(print());

        verify(postService, times(1)).delete(postId);
    }


    @Test
    public void get() throws Exception {
        Long postId = 1L;
        PostDto postDto = new PostDto();
        postDto.setTitle("Test Title");
        postDto.setContent("Test Content");

        when(postService.get(postId)).thenReturn(postDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/" + postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test Title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("Test Content"))
                .andDo(print());
    }


    @Test
    public void getList() throws Exception {
        PageRequestDto pageRequestDto = new PageRequestDto("title", "asc", 0, 10);
        Pageable pageable = pageRequestDto.toPageable();
        Page<PostDto> postDtoPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(postService.getList(pageable)).thenReturn(postDtoPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts")
                        .param("sortField", "title")
                        .param("sortOrder", "asc")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        verify(postService, times(1)).getList(pageable);
    }


    @Test
    public void getListByChildCategory() throws Exception {
        PageRequestDto pageRequestDto = new PageRequestDto("title", "asc", 0, 10);
        Pageable pageable = pageRequestDto.toPageable();
        Page<PostDto> postDtoPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Long categoryId = 1L;

        when(postService.getListByParentCategory(categoryId, pageable)).thenReturn(postDtoPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/category/" + categoryId + "/subcategories")
                        .param("sortField", "title")
                        .param("sortOrder", "asc")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        verify(postService, times(1)).getListByParentCategory(categoryId, pageable);
    }


    @Test
    public void getListByMember() throws Exception {
        PageRequestDto pageRequestDto = new PageRequestDto("title", "asc", 0, 10);
        Pageable pageable = pageRequestDto.toPageable();
        Page<PostDto> postDtoPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Long memberId = 1L;

        when(postService.getListByMember(memberId, pageable)).thenReturn(postDtoPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/member/" + memberId)
                        .param("sortField", "title")
                        .param("sortOrder", "asc")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        verify(postService, times(1)).getListByMember(memberId, pageable);
    }


    @Test
    public void searchListByKeyword() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<PostDto> postDtoPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        String keyword = "test";

        when(postService.searchListByKeyword(keyword, pageable)).thenReturn(postDtoPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/search")
                        .param("keyword", keyword)
                        .param("sort", "id,asc")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        verify(postService, times(1)).searchListByKeyword(keyword, pageable);
    }


    @Test
    public void react() throws Exception {
        Long memberId = 1L;
        Long postId = 1L;
        Boolean isLike = true;
        PostDto reactedPost = new PostDto();

        when(postService.react(memberId, postId, isLike)).thenReturn(reactedPost);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/" + postId + "/react")
                        .param("member_id", memberId.toString())
                        .param("is_like", isLike.toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print());

        verify(postService, times(1)).react(memberId, postId, isLike);
    }

}