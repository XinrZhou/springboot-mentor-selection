package com.example.mentorselection.service;

import com.example.mentorselection.entity.User;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminService {
    @Transactional
    public Mono<Void> resetTime(LocalDateTime time, long uid);

    @Transactional
    public Mono<Void> resetPassword(String number);

    public Mono<List<User>> addUsers(List<User> users);
}
