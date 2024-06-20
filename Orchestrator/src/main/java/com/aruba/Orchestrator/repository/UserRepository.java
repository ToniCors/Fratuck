package com.aruba.Orchestrator.repository;

import com.aruba.Orchestrator.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {

    Mono<User> findByUsername(String username);
    Mono<User> findByEmail(String email);

}