package com.JKS.community.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorCode> handleCustomException(CustomException e){
        log.error("handleCustomException", e);
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getErrorCode());
    }
}