package com.example.exception;

import lombok.SneakyThrows;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;

@Component
@Order(-2)
public class ExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        response.setStatusCode(HttpStatus.OK);

        DataBuffer buff = response.bufferFactory().allocateBuffer().write(throwable.getMessage().getBytes());

        response.getHeaders().setContentType(MediaType.APPLICATION_STREAM_JSON);
        return response.writeAndFlushWith(Mono.just(ByteBufMono.just(buff)));

    }
}
