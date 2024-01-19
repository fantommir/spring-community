package com.JKS.community.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponseDto> handleCustomException(CustomException e){
        log.error("handleCustomException, ErrorCode: {}, Message: {}", e.getErrorCode(), e.getMessage(), e);
        return ErrorResponseDto.toResponseEntity(e.getErrorCode());
    }
}