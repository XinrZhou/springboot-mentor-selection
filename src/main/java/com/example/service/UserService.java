package com.example.service;

import com.example.entity.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Mono<User> findByNumber(String number) {
        return userRepository.findByNumber(number);
    }

    public Mono<User> findById(long uid) {
        return userRepository.findById(uid);
    }
}
