package ru.doronin.poster.post;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.doronin.poster.user.SystemUser;

@Repository
public interface TweetRepository extends ReactiveMongoRepository<Tweet, String> {
    Flux<Tweet> findAllByAuthorOrderByModified(SystemUser author);
}
