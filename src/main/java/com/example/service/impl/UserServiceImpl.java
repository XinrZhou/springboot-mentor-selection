package com.example.service.impl;

import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Mono<User> getUser(String number) {
        return userRepository.findByNumber(number);
    }

    @Override
    public Mono<User> getUser(long uid) {
        return userRepository.findById(uid);
    }

}
