package com.example.mentorselection.exception;

import com.example.mentorselection.utils.ResultVO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptinHandler {

    @ExceptionHandler(Exception.class)
    public Mono<ResultVO> handleException(Exception e) {
        return Mono.just(ResultVO.error(400, e.getMessage()));
    }

    @ExceptionHandler(XException.class)
    public Mono<ResultVO> handleXException(XException e) {
        return Mono.just(ResultVO.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResultVO> handelDataIntegrityViolationException(DataIntegrityViolationException exception) {
        return Mono.just(ResultVO.error(409, exception.getMessage()));
    }
}

