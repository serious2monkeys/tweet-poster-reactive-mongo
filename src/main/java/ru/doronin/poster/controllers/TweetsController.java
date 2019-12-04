package ru.doronin.poster.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.doronin.poster.post.Tweet;
import ru.doronin.poster.post.TweetService;
import ru.doronin.poster.user.UserService;

import java.util.Collections;

/**
 * Plain old REST-controller
 */
@RestController
@Profile("controllers")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/tweets", produces = MediaType.APPLICATION_JSON_VALUE)
public class TweetsController {
    private final TweetService tweetService;
    private final UserService userService;

    @GetMapping
    public Flux<Tweet> getAllTweets() {
        return tweetService.loadAll();
    }

    /**
     * Publish new tweet
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> shareTweet(@RequestBody JsonNode jsonNode) {
        if (!jsonNode.has("tweet")) {
            return Mono.just(new ResponseEntity<>(Collections.singletonMap("message", "Text not specified"),
                    HttpStatus.BAD_REQUEST));
        }
        return Mono.just(new ResponseEntity<>(tweetService.create(jsonNode.get("tweet")
                .asText(), userService.getCurrent()), HttpStatus.CREATED));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> updateTweet(@RequestBody Tweet tweet) {
        if (tweet == null || tweet.getId() == null) {
            return Mono.just(new ResponseEntity<>(Collections.singletonMap("message", "Message identifier not specified"),
                    HttpStatus.BAD_REQUEST));
        }
        return userService.getCurrent().flatMap(user ->
                tweetService.load(tweet.getId()).flatMap(foundTweet -> {
                    if (user.getLogin().equals(foundTweet.getAuthor().getLogin())) {
                        foundTweet.setContent(tweet.getContent());
                        return Mono.just(new ResponseEntity<Object>(tweetService.save(foundTweet), HttpStatus.OK));
                    } else {
                        return Mono.just(new ResponseEntity<Object>(Collections.singletonMap("message", "Access denied"), HttpStatus.FORBIDDEN));
                    }
                }).switchIfEmpty(Mono.just(new ResponseEntity<>(Collections.singletonMap("message", "Tweet not found"), HttpStatus.BAD_GATEWAY)))
        );
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteTweet(@PathVariable("id") String tweetId) {
        if (tweetId == null) {
            return Mono.just(new ResponseEntity<>(Collections.singletonMap("message", "Message identifier not specified"),
                    HttpStatus.BAD_REQUEST));
        }
        return userService.getCurrent().flatMap(user ->
                tweetService.load(tweetId).flatMap(tweet -> {
                    if (user.getLogin().equals(tweet.getAuthor().getLogin())) {
                        return tweetService.delete(tweet)
                                .then(Mono.just(new ResponseEntity<Object>(Collections.singletonMap("message", "Tweet deleted"),
                                        HttpStatus.OK)));
                    } else {
                        return Mono.just(new ResponseEntity<Object>(Collections.singletonMap("message", "Access denied"), HttpStatus.FORBIDDEN));
                    }
                }).switchIfEmpty(Mono.just(new ResponseEntity<>(Collections.singletonMap("message", "Tweet not found"), HttpStatus.BAD_GATEWAY)))
        );
    }
}
