package com.example.service;

import com.example.config.SnowFlake;
import com.example.entity.StartTime;
import com.example.entity.User;
import com.example.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
public class InitService {
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StartTime startTime;

    @Transactional
    @EventListener(classes = ApplicationReadyEvent.class)
    public Mono<Void> onApplicationEvent() {
        String name = "admin";
        var number = "2046";
        return userRepository.count().flatMap(r -> {
            if (r == 0) {
                User admin = User.builder()
                        .name(name)
                        .number(number)
                        .password(encoder.encode(number))
                        .role(User.ADMIN)
                        .insertTime(LocalDateTime.now())
                        .selectTime(LocalDateTime.now().plusMonths(5))
                        .build();
                return userRepository.save(admin).then();
            }
            return userRepository.find(number).doOnSuccess(user -> {
                startTime.setStartTime(user.getSelectTime());
            }).then();
        });
    }
}