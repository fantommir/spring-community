package com.JKS.community.controller;


import com.JKS.community.dto.MemberDto;
import com.JKS.community.dto.MemberFormDto;
import com.JKS.community.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 API")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    @Operation(summary = "회원 가입")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 회원입니다.")
    })
    public ResponseEntity<MemberDto> register(@Valid @ModelAttribute MemberFormDto memberFormDto) {
        MemberDto registeredMember = memberService.register(memberFormDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredMember);
    }

    @GetMapping("/")
    @Operation(summary = "회원 목록 조회")
    @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공")
    public ResponseEntity<Page<MemberDto>> getList(Pageable pageable) {
        Page<MemberDto> memberPage = memberService.getList(pageable);
        return ResponseEntity.ok(memberPage);
    }

    @GetMapping("/search")
    @Operation(summary = "회원 이름 검색")
    @ApiResponse(responseCode = "200", description = "회원 이름 검색 성공")
    public Page<MemberDto> getListByName(@RequestParam String name, Pageable pageable){
        return this.memberService.getListByName(name, pageable);
    }

    @GetMapping("/{memberId}")
    @Operation(summary = "회원 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 회원을 찾을 수 없습니다.")
    })
    public ResponseEntity<MemberDto> get(@PathVariable Long memberId) {
        MemberDto member = memberService.get(memberId);
        return ResponseEntity.ok(member);
    }

    @PutMapping("/{memberId}")
    @Operation(summary = "회원 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "해당 회원을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 비밀번호입니다.")
    })
    public ResponseEntity<MemberDto> update(@PathVariable Long memberId, @Valid @RequestBody MemberFormDto memberFormDto) {
        MemberDto updatedMember = memberService.update(memberId, memberFormDto);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{memberId}")
    @Operation(summary = "회원 탈퇴")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "404", description = "해당 회원을 찾을 수 없습니다.")
    })
    public ResponseEntity<Void> withdrawal(@PathVariable Long memberId) {
        memberService.withdrawal(memberId);
        return ResponseEntity.noContent().build();
    }
}