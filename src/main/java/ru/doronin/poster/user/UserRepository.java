package ru.doronin.poster.user;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<SystemUser, String> {
    Mono<SystemUser> findFirstByLogin(String login);
}
