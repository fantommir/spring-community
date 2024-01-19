package com.JKS.community.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ErrorResponseDto {

    private int status;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponseDto> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponseDto.builder()
                        .status(errorCode.getStatus().value())
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}
