package com.tao.common.retry;

import com.tao.common.core.common.message.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class RetryExceptionHandler {

    @Order(1)
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NeedRetryException.class)
    public Result handleBaseException(NeedRetryException e, HttpServletRequest httpServletRequest, HandlerMethod handler) {
        log.error(e.getMessage(), e);
        return Result.error(e.getCode(), "处理锁定中");
    }
}
