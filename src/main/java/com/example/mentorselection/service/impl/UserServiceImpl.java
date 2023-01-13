package com.example.mentorselection.service.impl;

import com.example.mentorselection.entity.StartTime;
import com.example.mentorselection.entity.User;
import com.example.mentorselection.exception.XException;
import com.example.mentorselection.repository.UserRepository;
import com.example.mentorselection.service.UserService;
import com.example.mentorselection.utils.ResultVO;
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
        return userRepository.findByNumber(number);
    }

    @Override
    public Mono<User> getUser(long uid) {
        return userRepository.findById(uid);
    }

    @Override
    public Mono<Void> resetPassword(long uid, String pwd) {
        return userRepository.updatePassword(uid,passwordEncoder.encode(pwd))
                .doOnSuccess(user -> {
                    if (user == 0) {
                        throw new XException(ResultVO.BAD_REQUEST, "修改失败，请稍后再试");
                    }
                }).then();
    }

    @Override
    public Mono<List<User>> listUsers() {
        return userRepository.findAllByRole(User.TEACHER).collectList();
    }

    @Override
    public Mono<User> select(long uid, long tid) {
        Mono<User> student = userRepository.findById(uid)
                .doOnSuccess(s -> {
                    if (s == null) {
                        throw new XException(ResultVO.BAD_REQUEST, "学生不存在");
                    }
                    if (s.getTeacherId() != null) {
                        throw new XException(ResultVO.BAD_REQUEST, "导师不可重复选择");
                    }
                });
        Mono<User> teacher = userRepository.findById(tid)
                .doOnSuccess(t -> {
                    if (t == null) {
                        throw new XException(ResultVO.BAD_REQUEST, "导师不存在，选择失败");
                    }
                    if (t.getTotal() - t.getCount() == 0) {
                        throw new XException(ResultVO.BAD_REQUEST, "该导师名额已满，请重新选择");
                    }
                });

        return student.flatMap(s ->
                    teacher.flatMap(t ->
                                    userRepository.updateTeacherCount(tid).flatMap(r ->
                                            Mono.just(t.getName())))
                            .flatMap(name -> {
                                s.setTeacherId(tid);
                                s.setTeacherName(name);
                                s.setSelectTime(LocalDateTime.now());
                                return userRepository.save(s);
                            }));
    }


}
