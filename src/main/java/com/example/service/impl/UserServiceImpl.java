package com.example.service.impl;

import com.example.entity.StartTime;
import com.example.entity.User;
import com.example.exception.XException;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StartTime startTime;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Mono<User> getUser(String number) {
        return userRepository.find(number);
    }

    @Override
    public Mono<User> getUser(long uid) {
        return userRepository.findById(uid);
    }

    @Override
    public Mono<Void> resetTime(LocalDateTime time, long uid) {
        return userRepository.findById(uid).flatMap(user -> {
            startTime.setStartTime(time);
            user.setSelectTime(time);
            return userRepository.save(user).then();
        });
    }

    @Override
    public Mono<Void> resetPassword(String number) {
        return userRepository.find(number)
                        .doOnSuccess(user -> {
                            if (user == null) {
                                throw new XException("账号不存在，重置失败");
                            }
                            user.setPassword(passwordEncoder.encode(number));
                        }).then();
    }

    @Override
    public Mono<List<User>> addUsers(List<User> users) {
        return userRepository.saveAll(users).collectList();

    }

    @Override
    public Mono<Void> resetPwd(String pwd, long uid) {
        return userRepository.findById(uid)
                .map(u -> {
                    u.setPassword(passwordEncoder.encode(pwd));
                    return u;
                }).then();
    }

    @Override
    public Mono<List<User>> listUsers(int role) {
        return userRepository.list(role).collectList();
    }


}
