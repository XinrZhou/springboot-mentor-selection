package com.example.mentorselection.repository;

import com.example.mentorselection.entity.User;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByNumber(String number);

    Flux<User> findAllByTeacherId(long tid);

    Flux<User> findAllByTeacherNameIsNullAndRole(Integer role);

    Flux<User> findAllByRole(Integer role);

    @Modifying
    @Query("update user u set u.count=u.count+1 where u.total-u.count>0 and id=:tid;")
    Mono<Integer> updateTeacherCount(long tid);

    @Modifying
    @Query("update user u set u.password=:password where u.number=:number;")
    Mono<Integer> updatePassword(String number, String password);

    @Modifying
    @Query("update user u set u.password=:password where u.id=:uid;")
    Mono<Integer> updatePassword(long uid, String password);

    @Modifying
    @Query("update user u set u.count=0 where u.role=5;")
    Mono<Integer> updateTeachersCount();

}
