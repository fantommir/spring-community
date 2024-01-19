package com.JKS.community.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-001", "해당 회원을 찾을 수 없습니다."),
    MEMBER_EMAIL_DUPLICATION(HttpStatus.CONFLICT, "MEMBER-002", "이미 존재하는 회원입니다."),
    MEMBER_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "MEMBER-003", "비밀번호가 일치하지 않습니다."),
    MEMBER_PASSWORD_DUPLICATION(HttpStatus.CONFLICT, "MEMBER-004", "이미 사용 중인 비밀번호입니다."),
    MEMBER_PASSWORD_INVALID(HttpStatus.BAD_REQUEST, "MEMBER-005", "비밀번호 형식이 올바르지 않습니다."),
    MEMBER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "MEMBER-006", "접근 권한이 없습니다."),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST-001", "해당 게시글을 찾을 수 없습니다."),

    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY-001", "해당 카테고리를 찾을 수 없습니다."),

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT-001", "해당 댓글을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
