package com.loogingko.ncjd.exception;

import cn.hutool.core.util.StrUtil;
import com.loogingko.ncjd.model.bo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 针对所有 Controller 的异常统一处理器
 * @author LiuRunYu 2026-04-10
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 @Valid 校验失败
     * @author LiuRunYu 2026-05-04
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException ex) {
        // 取第一条错误信息
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage) // 例如：密码长度6-20位
                .collect(Collectors.joining("; "));
        String error = StrUtil.format("参数校验失败: {}", msg);
        log.warn(error);
        return R.fail(error).code(400);
    }
    
    @ExceptionHandler(BindException.class)
    public ResponseEntity<R> handleBind(BindException ex) {
        String message = ex.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: {}", message);
        return ResponseEntity.badRequest().body(R.fail(message).code(400));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<R> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("非法参数: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(R.fail(ex.getMessage()).code(400));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<R> handleNotFound(NotFoundException ex) {
        log.warn("资源不存在: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(R.fail(ex.getMessage()).code(404));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<R> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("数据完整性 violation", ex);
        return ResponseEntity.badRequest().body(R.fail("数据冲突").code(400));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<R> handleException(Exception ex) {
        log.error("系统异常", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(R.fail("系统异常").code(500));
    }
}

