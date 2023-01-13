package com.example.mentorselection.service.impl;

import com.example.mentorselection.entity.StartTime;
import com.example.mentorselection.entity.User;
import com.example.mentorselection.exception.XException;
import com.example.mentorselection.repository.UserRepository;
import com.example.mentorselection.service.AdminService;
import com.example.mentorselection.utils.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StartTime startTime;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        return userRepository.updatePassword(number, passwordEncoder.encode(number))
                .doOnSuccess(user -> {
                    if (user == 0) {
                        throw new XException(ResultVO.BAD_REQUEST, "用户不存在，重置失败");
                    }
                }).then();
    }

    @Override
    public Mono<List<User>> addUsers(List<User> users) {
        return userRepository.saveAll(users).collectList();
    }
}
