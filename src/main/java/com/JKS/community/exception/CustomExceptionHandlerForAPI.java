package com.JKS.community.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandlerForAPI {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponseDto> handleCustomException(CustomException e){
        log.error("handleCustomException, ErrorCode: {}, Message: {}", e.getErrorCode(), e.getMessage(), e);
        return ErrorResponseDto.toResponseEntity(e.getErrorCode());
    }
}