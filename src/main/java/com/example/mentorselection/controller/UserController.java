package com.example.mentorselection.controller;

import com.example.mentorselection.entity.StartTime;
import com.example.mentorselection.entity.User;
import com.example.mentorselection.exception.XException;
import com.example.mentorselection.service.UserService;
import com.example.mentorselection.utils.JwtUtil;
import com.example.mentorselection.utils.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
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
    Mono<ResultVO> login(@RequestBody User user, ServerHttpResponse response) {
        return userService.getUser(user.getNumber())
                .doOnSuccess(u -> {
                    if (u == null || !passwordEncoder.matches( user.getPassword(), u.getPassword())) {
                        throw new XException(ResultVO.UNAUTHORIZED, "账号密码错误");
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
                    return ResultVO.success();
                });
    }

    /**
     * 获取用户信息
     * @param uid
     * @param role
     * @return
     */
    @GetMapping("/info")
    Mono<ResultVO> getInfo(@RequestAttribute("uid") long uid, @RequestAttribute("role") int role) {
        return userService.getUser(uid)
                .flatMap(user -> {
                    return Mono.just(ResultVO.success(Map.of("user", user,"starttime",startTime.getStartTime())));
                });
    }

    /**
     * 修改密码
     * @param pwd
     * @param uid
     * @return
     */
    @PutMapping("/password/{pwd}")
    Mono<ResultVO> resetPwd(@PathVariable String pwd, @RequestAttribute("uid") long uid) {
        return userService.resetPassword(uid, pwd)
                .then(Mono.just(ResultVO.success()));
    }

    /**
     * 获取导师列表
     * @param role
     * @return
     */
    @GetMapping("/teachers")
    public Mono<ResultVO> getTeacherList(@RequestAttribute("role") int role) {
        return role == User.STUDENT && startTime.getStartTime().isAfter(LocalDateTime.now())
                ? Mono.just(ResultVO.error(ResultVO.BAD_REQUEST ,"开始时间" + startTime.getStartTime()))
                : userService.listUsers()
                .map(users -> ResultVO.success(Map.of("teachers", users)));
    }

    /**
     * 选导师
     * @param uid
     * @param tid
     * @return
     */
    @PutMapping("/teachers/{tid}")
    public Mono<ResultVO> selectTeacher(@RequestAttribute("uid") long uid, @PathVariable long tid) {
        return userService.select(uid, tid)
                .map(user -> ResultVO.success(Map.of("user", user)));
    }

}
