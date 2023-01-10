package com.example.service;

import com.example.entity.User;
import reactor.core.publisher.Mono;

public interface UserService {

    public Mono<User> getUser(String number);

    public Mono<User> getUser(long uid);
}
