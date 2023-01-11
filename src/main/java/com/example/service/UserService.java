package com.example.service;

import com.example.entity.User;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {

    public Mono<User> getUser(String number);

    public Mono<User> getUser(long uid);

    public Mono<Void> resetTime(LocalDateTime time, long uid);

    public Mono<Void> resetPassword(String number);

    public Mono<List<User>> addUsers(List<User> users);

    public Mono<Void> resetPwd(String pwd, long uid);

    public Mono<List<User>> listUsers(int role);
}
