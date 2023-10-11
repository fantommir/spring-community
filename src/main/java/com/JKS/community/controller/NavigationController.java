package com.JKS.community.controller;


import com.JKS.community.dto.*;
import com.JKS.community.security.CustomUserDetails;
import com.JKS.community.service.CategoryService;
import com.JKS.community.service.MemberService;
import com.JKS.community.service.PostService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute MemberFormDto memberFormDto) {
        memberService.register(memberFormDto);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin() {
        return "redirect:/";
    }

    @GetMapping("/info")
    public String myInfo(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";
        MemberDto memberDto = memberService.get(userDetails.getId());
        model.addAttribute("memberDto", memberDto);
        return "info";
    }

    @GetMapping("/info/{memberId}")
    public String info(@PathVariable Long memberId, Model model) {
        MemberDto memberDto = memberService.get(memberId);
        model.addAttribute("memberDto", memberDto);
        return "info-form";
    }

    @GetMapping("/info/{memberId}/edit")
    public String editInfo(@PathVariable Long memberId, Model model,
                           @ModelAttribute MemberFormDto memberFormDto) {
        MemberDto updatedMemberDto = memberService.update(memberId, memberFormDto);
        model.addAttribute("memberDto", updatedMemberDto);
        return "info";
    }

    @GetMapping("/category/{categoryId}")
    public String postsByCategory(@PathVariable Long categoryId, Model model) {
        Pageable pageable = PageRequest.of(0, 20);
        CategoryDto categoryDto = categoryService.get(categoryId);
        Page<PostDto> postDtoPage = postService.getListByParentCategory(categoryId, pageable);
        model.addAttribute("category", categoryDto);
        model.addAttribute("postList", postDtoPage);
        return "posts-by-category";
    }

    // 게시물 조회 컨트롤러
    @GetMapping("/post/{postId}")
    public String postView(@PathVariable Long postId, Model model,
                           @AuthenticationPrincipal CustomUserDetails userDetails, HttpServletRequest request, HttpServletResponse response) {
        PostDto postDto = postService.get(postId);
        model.addAttribute("postDto", postDto);
        if (userDetails != null) {
            model.addAttribute("memberId", userDetails.getId());
        }
        // 쿠키 확인
        Cookie[] cookies = request.getCookies();
        boolean isVisited = false;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("post" + postId)) {
                    isVisited = true;
                    break;
                }
            }
        }
        // 쿠키가 없으면 생성하고 조회수 증가
        if (!isVisited) {
            Cookie cookie = new Cookie("post" + postId, "true");
            cookie.setMaxAge(60 * 60); // 만료 시간 1시간
            cookie.setPath("/");
            response.addCookie(cookie);
            postService.increaseViewCount(postId);
        }
        return "post-view";
    }

    // 게시물 작성
    @GetMapping("/{categoryId}/create")
    public String createPost(@PathVariable String categoryId, Model model,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        CategoryDto categoryDto = categoryService.get(Long.valueOf(categoryId));
        model.addAttribute("postDto", new PostDto());
        model.addAttribute("category", categoryDto);
        if (userDetails != null) model.addAttribute("memberId", userDetails.getId());

        return "post-form";
    }

    // 게시물 수정
    @GetMapping("/post/{postId}/edit")
    public String editPost(@PathVariable Long postId, Model model,
                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostDto postDto = postService.get(postId);
        CategoryDto categoryDto = categoryService.get(postDto.getParentCategoryId());
        model.addAttribute("postDto", postDto);
        model.addAttribute("category", categoryDto);
        if (userDetails != null) model.addAttribute("memberId", userDetails.getId());

        return "post-form";
    }
}
