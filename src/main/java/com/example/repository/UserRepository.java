package com.example.repository;

import com.example.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    // 根据账号查找
    @Query("select * from user u where u.number=:number;")
    Mono<User> findByNumber(String number);


}
