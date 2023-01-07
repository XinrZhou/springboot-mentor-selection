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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private StartTime startTime;


    @PostMapping("/login")
    Mono<Result> login(@RequestBody User user, ServerHttpResponse response) {
        return userService.findByNumber(user.getNumber())
                .doOnSuccess(u -> {
                    if (u == null || !u.getPassword().equals(user.getPassword())) {
                        throw new XException("账号密码错误");
                    }
                }).map(u -> {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("uid", u.getId().toString());
                    map.put("role", u.getRole().toString());
                    String token = JwtUtil.generateToken(map);
                    response.getHeaders().add("token", token);


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
        Mono<User> userMono = userService.findById(uid);

        return userMono.flatMap(user -> {
            return Mono.just(Result.success(Map.of("user", user)));
        });
    }

}
