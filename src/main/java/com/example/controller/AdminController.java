package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import com.example.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 重置开始时间
     * @param time
     * @param uid
     * @return
     */
    @PutMapping("/starttime/{time}")
    public Mono<Result> putStartTime(@PathVariable String time, @RequestAttribute("uid") long uid) {
        LocalDateTime startTime = LocalDateTime.parse(time);
        return userService.resetTime(startTime, uid)
                .then(Mono.just(Result.success(Map.of("time", startTime))));
    }

    /**
     * 重置密码
     * @param number
     * @return
     */
    @PutMapping("/password/{number}")
    public Mono<Result> putPassword(@PathVariable String number) {
        return  userService.resetPassword(number)
                .then(Mono.just(Result.success("重置成功")));
    }

    /**
     * 添加老师
     * @param users
     * @return
     */
    @PostMapping("/teachers")
    public Mono<Result> saveTeachers(@RequestBody List<User> users) {
        List<User> userList = users.stream().map(u -> {
            u.setPassword(passwordEncoder.encode(u.getNumber()));
            u.setRole(User.TEACHER);
            u.setCount(0);
            u.setInsertTime(LocalDateTime.now());
            return u;
        }).collect(Collectors.toList());
        return userService.addUsers(userList)
                .then(Mono.just(Result.success("添加成功")));
    }

    /**
     * 添加学生
     * @param users
     * @return
     */
    @PostMapping("/students")
    public Mono<Result> saveStudents(@RequestBody List<User> users) {
        List<User> userList = users.stream().map(u -> {
            u.setPassword(passwordEncoder.encode(u.getNumber()));
            u.setRole(User.STUDENT);
            u.setInsertTime(LocalDateTime.now());
            return u;
        }).collect(Collectors.toList());
        return userService.addUsers(userList)
                .then(Mono.just(Result.success("添加成功")));
    }
}
