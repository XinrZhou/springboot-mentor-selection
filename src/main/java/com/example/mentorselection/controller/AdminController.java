package com.example.mentorselection.controller;

import com.example.mentorselection.entity.User;
import com.example.mentorselection.service.AdminService;
import com.example.mentorselection.utils.ResultVO;
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
    private AdminService adminService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * 重置开始时间
     * @param time
     * @param uid
     * @return
     */
    @PutMapping("/starttime/{time}")
    public Mono<ResultVO> putStartTime(@PathVariable String time, @RequestAttribute("uid") long uid) {
        LocalDateTime startTime = LocalDateTime.parse(time);
        return adminService.resetTime(startTime, uid)
                .then(Mono.just(ResultVO.success(Map.of("time", startTime))));
    }


    /**
     * 重置密码
     * @param number
     * @return
     */
    @PutMapping("/password/{number}")
    public Mono<ResultVO> putPassword(@PathVariable String number) {
        return  adminService.resetPassword(number)
                .then(Mono.just(ResultVO.success("重置成功")));
    }

    /**
     * 添加老师
     * @param users
     * @return
     */
    @PostMapping("/teachers")
    public Mono<ResultVO> saveTeachers(@RequestBody List<User> users) {
        List<User> userList = users.stream().map(u -> {
            u.setPassword(passwordEncoder.encode(u.getNumber()));
            u.setRole(User.TEACHER);
            u.setCount(0);
            u.setInsertTime(LocalDateTime.now());
            return u;
        }).collect(Collectors.toList());
        return adminService.addUsers(userList)
                .then(Mono.just(ResultVO.success("添加成功")));
    }

    /**
     * 添加学生
     * @param users
     * @return
     */
    @PostMapping("/students")
    public Mono<ResultVO> saveStudents(@RequestBody List<User> users) {
        List<User> userList = users.stream().map(u -> {
            u.setPassword(passwordEncoder.encode(u.getNumber()));
            u.setRole(User.STUDENT);
            u.setInsertTime(LocalDateTime.now());
            return u;
        }).collect(Collectors.toList());
        return adminService.addUsers(userList)
                .then(Mono.just(ResultVO.success("添加成功")));
    }

    /**
     * 重置数据
     * @return
     */
    @PutMapping("/reset")
    public Mono<ResultVO> resetTeacherAndStudentData() {
        return adminService.resetData()
                .then(Mono.just(ResultVO.success("重置成功")));
    }
}
