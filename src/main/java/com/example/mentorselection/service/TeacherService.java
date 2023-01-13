package com.example.mentorselection.service;

import com.example.mentorselection.entity.User;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TeacherService {

    public Mono<List<User>> listUsersByTid(long tid);

    public Mono<List<User>> listUnselected();

    public Mono<List<User>> listAll();
}
