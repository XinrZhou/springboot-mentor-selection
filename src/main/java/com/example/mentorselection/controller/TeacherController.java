package com.example.mentorselection.controller;

import com.example.mentorselection.service.TeacherService;
import com.example.mentorselection.utils.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    /**
     * 该导师的学生信息
     * @param uid
     * @return
     */
    @GetMapping("/students")
    public Mono<ResultVO> getStudents(@RequestAttribute("uid") long uid) {
        return teacherService.listUsersByTid(uid)
                .map(students -> ResultVO.success(Map.of("students", students)));
    }

    /**
     * 未选学生信息
     * @return
     */
    @GetMapping("/unselected")
    public Mono<ResultVO> getUnselected() {
        return teacherService.listUnselected()
                .map(students -> ResultVO.success(Map.of("students", students)));
    }

    /**
     * 所有学生选导师信息
     * @return
     */
    @GetMapping("/allstudents")
    public Mono<ResultVO> getAll() {
        return teacherService.listAll()
                .map(students -> ResultVO.success(Map.of("students", students)));
    }


}
