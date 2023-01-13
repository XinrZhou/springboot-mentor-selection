package com.example.mentorselection.service.impl;

import com.example.mentorselection.entity.User;
import com.example.mentorselection.repository.UserRepository;
import com.example.mentorselection.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Mono<List<User>> listUsersByTid(long tid) {
        return userRepository.findAllByTeacherId(tid).collectList();
    }

    @Override
    public Mono<List<User>> listUnselected() {
        return userRepository.findAllByTeacherNameIsNullAndRole(User.STUDENT).collectList();
    }

    @Override
    public Mono<List<User>> listAll() {
        return userRepository.findAllByRole(User.STUDENT).collectList();
    }
}
