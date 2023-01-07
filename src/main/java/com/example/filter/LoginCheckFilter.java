package com.example.filter;

import com.example.entity.User;
import com.example.exception.XException;
import com.example.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@Component
@Slf4j
public class LoginCheckFilter implements WebFilter {

    // 路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    String[] urls = new String[] {
            "/api/login"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, request.getPath().pathWithinApplication().value());
            if(match) {
                return chain.filter(exchange);
            }
        }

        String token = request.getHeaders().getFirst("token");
        if(token != null) {
            User user = JwtUtil.verifyToken(token);
            exchange.getAttributes().put("uid",user.getId());
            exchange.getAttributes().put("role",user.getRole());
            return chain.filter(exchange);
        }

        throw new XException("未登录");
    }
}
