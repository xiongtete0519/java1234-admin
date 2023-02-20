package com.java1234.common.exception;

import com.java1234.entity.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value =RuntimeException.class)
    public R handler(RuntimeException e){
        log.error("运行时异常:------{}",e.getMessage());
        return R.error(e.getMessage());
    }
}
