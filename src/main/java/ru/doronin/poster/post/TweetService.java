package ru.doronin.poster.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.doronin.poster.user.SystemUser;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class TweetService {
    private final TweetRepository tweetRepository;

    public Mono<Tweet> save(Tweet tweet) {
        preconditioning(tweet);
        return tweetRepository.save(tweet);
    }

    private void preconditioning(Tweet tweet) {
        Instant moment = Instant.now();
        if (tweet.getCreated() == null) {
            tweet.setCreated(moment);
        }
        tweet.setModified(moment);
    }

    public Flux<Tweet> save(Iterable<Tweet> tweets) {
        tweets.forEach(this::preconditioning);
        return tweetRepository.saveAll(tweets);
    }

    public Mono<Tweet> load(String id) {
        return tweetRepository.findById(id);
    }

    public Flux<Tweet> loadAll() {
        return tweetRepository.findAll(new Sort(Sort.Direction.DESC, "modified"));
    }

    public Mono<Void> delete(Tweet tweet) {
        return tweetRepository.delete(tweet);
    }

    public Long countRecords() {
        return tweetRepository.count().block();
    }

    public Mono<Tweet> create(String text, Mono<SystemUser> user) {
        return user.flatMap(author -> save(Tweet.builder()
                .author(author)
                .content(text)
                .build()));
    }
}
