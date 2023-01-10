package com.example.controller;

import com.example.entity.StartTime;
import com.example.entity.User;
import com.example.exception.XException;
import com.example.service.UserService;
import com.example.utils.JwtUtil;
import com.example.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private StartTime startTime;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    Mono<Result> login(@RequestBody User user, ServerHttpResponse response) {
        return userService.getUser(user.getNumber())
                .doOnSuccess(u -> {
                    System.out.println(passwordEncoder.matches( user.getPassword(), u.getPassword()));
                    if (u == null || !passwordEncoder.matches( user.getPassword(), u.getPassword())) {
                        throw new XException("账号密码错误");
                    }
                }).map(u -> {
                    String jwt = JwtUtil.createJWT(Map.of("uid", u.getId(), "role", u.getRole()));
                    response.getHeaders().add("token", jwt);

                    String role = "";
                    switch (u.getRole()) {
                        case User.ADMIN:
                            role = "ppYMg";
                            break;
                        case User.TEACHER:
                            role = "nU0vt";
                            break;
                        case User.STUDENT:
                            role = "Yo87M";
                            break;
                    }
                    response.getHeaders().add("role",role);
                    return Result.success();
                });
    }

    @GetMapping("/info")
    Mono<Result> getInfo(@RequestAttribute("uid") long uid, @RequestAttribute("role") int role) {
        Mono<User> userMono = userService.getUser(uid);

        return userMono.flatMap(user -> {
            return Mono.just(Result.success(Map.of("user", user)));
        });
    }

}
