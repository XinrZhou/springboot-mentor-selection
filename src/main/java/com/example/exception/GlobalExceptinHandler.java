package com.example.exception;

import com.example.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptinHandler {

    @ExceptionHandler(Exception.class)
    public Mono<Result> handleException(Exception e) {
        return Mono.just(Result.error( e.getMessage()));
    }

    @ExceptionHandler(XException.class)
    public Mono<Result> handleXException(XException e) {
        return Mono.just(Result.error(e.getMessage()));
    }

}

