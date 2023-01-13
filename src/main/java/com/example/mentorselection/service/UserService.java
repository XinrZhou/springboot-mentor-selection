package com.example.mentorselection.service;

import com.example.mentorselection.entity.User;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {

    public Mono<User> getUser(String number);

    public Mono<User> getUser(long uid);


    @Transactional
    public Mono<Void> resetPassword(long uid, String pwd);

    public Mono<List<User>> listUsers();

    @Transactional
    public Mono<User> select(long uid, long tid);

}
