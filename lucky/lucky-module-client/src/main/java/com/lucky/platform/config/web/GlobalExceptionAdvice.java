//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.lucky.platform.config.web;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Set;

/**
 * interceptor
 * @author Loki
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionAdvice {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

    public GlobalExceptionAdvice() {
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResultModel<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("缺少请求参数", e);
        return ResultCode.COMMON_PARAM_103.toResult();
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResultModel<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        if (log.isDebugEnabled()) {
            log.debug("参数解析失败", e);
        } else {
            log.warn("参数解析失败", e);
        }

        return ResultCode.COMMON_PARAM_102.toResult();
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        if (log.isDebugEnabled()) {
            log.debug("参数验证失败", e);
        } else {
            log.warn("参数验证失败", e);
        }

        BindingResult result = e.getBindingResult();
        String message = ((ObjectError)result.getAllErrors().get(0)).getDefaultMessage();
        return ResultModel.failure(ResultCode.COMMON_PARAM_101.getCode(), message);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({BindException.class})
    public ResultModel<?> handleBindException(BindException e) {
        log.error("参数绑定失败", e);
        return ResultCode.COMMON_PARAM_102.toResult();
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({ConstraintViolationException.class})
    public ResultModel<?> handleServiceException(ConstraintViolationException e) {
        log.error("参数验证失败", e);
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = (ConstraintViolation)violations.iterator().next();
        String message = violation.getMessage();
        return ResultModel.failure(message);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({ValidationException.class})
    public ResultModel<?> handleValidationException(ValidationException e) {
        log.error("参数验证失败", e);
        return ResultCode.COMMON_PARAM_101.toResult();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoHandlerFoundException.class})
    public ResultModel<?> noHandlerFoundException(NoHandlerFoundException e) {
        log.error("Not Found", e);
        return ResultCode.COMMON_PARAM_103.toResult();
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResultModel<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("不支持当前请求方法", e);
        return ResultCode.COMMON_REQUEST_001.toResult();
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public ResultModel<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error("不支持当前媒体类型", e);
        return ResultCode.COMMON_REQUEST_001.toResult();
    }


    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResultModel<?> defaultErrorHandler(MaxUploadSizeExceededException e) {
        log.info(e.getMessage());
        return ResultCode.COMMON_REQUEST_060.toResult();
    }

    @ExceptionHandler({Exception.class})
    public ResultModel<?> defaultErrorHandler(Exception e) {
        log.error("未知异常", e);
        return ResultCode.COMMON_REQUEST_050.toResult();
    }
}
