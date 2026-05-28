package com.atguigu.yygh.common.exception;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理器
 * 统一处理业务异常和系统异常，返回标准 Result 格式
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 系统异常兜底处理，避免异常堆栈直接暴露给前端
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        log.error("系统异常", e);
        return Result.fail();
    }

    /**
     * 自定义业务异常处理
     */
    @ExceptionHandler(YyghException.class)
    @ResponseBody
    public Result error(YyghException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.build(e.getCode(), e.getMessage());
    }
}
