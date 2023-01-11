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
import java.time.format.DateTimeFormatter;
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

    /**
     * 登录
     * @param user
     * @param response
     * @return
     */
    @PostMapping("/login")
    Mono<Result> login(@RequestBody User user, ServerHttpResponse response) {
        return userService.getUser(user.getNumber())
                .doOnSuccess(u -> {
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

    /**
     * 获取用户信息
     * @param uid
     * @param role
     * @return
     */
    @GetMapping("/info")
    Mono<Result> getInfo(@RequestAttribute("uid") long uid, @RequestAttribute("role") int role) {
        return userService.getUser(uid)
                .flatMap(user -> {
                    return Mono.just(Result.success(Map.of("user", user,"starttime",startTime.getStartTime())));
                });
    }

    /**
     * 修改密码
     * @param pwd
     * @param uid
     * @return
     */
    @PutMapping("/password/{pwd}")
    Mono<Result> resetPwd(@PathVariable String pwd, @RequestAttribute("uid") long uid) {
        return userService.resetPwd(pwd, uid)
                .then(Mono.just(Result.success()));
    }

    /**
     * 获取导师列表
     * @param role
     * @return
     */
    @GetMapping("/teachers")
    public Mono<Result> getTeacherList(@RequestAttribute("role") int role) {
        return role == User.STUDENT && startTime.getStartTime().isAfter(LocalDateTime.now())
                ? Mono.just(Result.error("未开始，开始时间：" + startTime.getStartTime()))
                : userService.listUsers(User.TEACHER)
                .map(users -> Result.success(Map.of("teachers", users)));
    }

}
