package com.JKS.community.controller;


import com.JKS.community.dto.MemberDto;
import com.JKS.community.dto.MemberFormDto;
import com.JKS.community.exception.InvalidIdException;
import com.JKS.community.exception.InvalidPasswordException;
import com.JKS.community.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<MemberDto> register(@Valid @RequestBody MemberFormDto memberFormDto) {
        MemberDto registeredMember = memberService.register(memberFormDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredMember);
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute MemberFormDto memberFormDto, Model model, HttpServletRequest request) {
        try {
            MemberDto loggedInMember = memberService.login(memberFormDto);
            // 세션에 로그인 정보 저장
            HttpSession session = request.getSession();
            session.setAttribute("member", loggedInMember);

            return "redirect:/";
        } catch (InvalidIdException | InvalidPasswordException e) {
            model.addAttribute("loginId", memberFormDto.getLoginId());
            model.addAttribute("errorMessage", e.getMessage());

            return "login";
        }
    }


    @GetMapping("/")
    public ResponseEntity<Page<MemberDto>> getList(Pageable pageable) {
        Page<MemberDto> memberPage = memberService.getList(pageable);
        return ResponseEntity.ok(memberPage);
    }

    @GetMapping("/search")
    public Page<MemberDto> getListByName(@RequestParam String name, Pageable pageable){
        return this.memberService.getListByName(name, pageable);
    }


    @GetMapping("/{memberId}")
    public ResponseEntity<MemberDto> get(@PathVariable Long memberId) {
        MemberDto member = memberService.get(memberId);
        return ResponseEntity.ok(member);
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<MemberDto> update(@PathVariable Long memberId, @Valid @RequestBody MemberFormDto memberFormDto) {
        MemberDto updatedMember = memberService.update(memberId, memberFormDto);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> withdrawal(@PathVariable Long memberId) {
        memberService.withdrawal(memberId);
        return ResponseEntity.noContent().build();
    }
}
