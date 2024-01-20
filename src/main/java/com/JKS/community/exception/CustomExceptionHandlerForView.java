package com.JKS.community.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandlerForView {

    @ExceptionHandler(CustomException.class)
    public ModelAndView handleCustomException(CustomException e){
        log.error("handleCustomException, ErrorCode: {}, Message: {}", e.getErrorCode(), e.getMessage(), e);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", e.getErrorCode().getStatus().value());
        modelAndView.addObject("message", e.getErrorCode().getMessage());
        return modelAndView;
    }
}
