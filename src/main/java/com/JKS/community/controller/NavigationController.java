package com.JKS.community.controller;


import com.JKS.community.dto.CategoryDto;
import com.JKS.community.dto.PostDto;
import com.JKS.community.service.CategoryService;
import com.JKS.community.service.MemberService;
import com.JKS.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class NavigationController {

    private final MemberService memberService;
    private final PostService postService;
    private final CategoryService categoryService;

    @GetMapping
    public String home(Model model) {
        List<CategoryDto> dtoList = categoryService.getList();
        model.addAttribute("categoryList", dtoList);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/category/{categoryId}")
    public String category(@PathVariable Long categoryId, Model model) {
        Pageable pageable = PageRequest.of(0, 20);
        CategoryDto categoryDto = categoryService.get(categoryId);
        Page<PostDto> postDtoPage = postService.getListByParentCategory(categoryId, pageable);
        model.addAttribute("tabs", Arrays.asList("all", "잡담", "정보", "사진", "공지"));
        model.addAttribute("category", categoryDto);
        model.addAttribute("postList", postDtoPage);
        return "posts-by-category";
    }
}
