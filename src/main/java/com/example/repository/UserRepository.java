package com.example.repository;

import com.example.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    // id查找
    @Query("select * from user u where u.number=:number;")
    Mono<User> find(String number);

    // role查找
    @Query("select * from user u where u.role=:role;")
    Flux<User> list(int role);

}
