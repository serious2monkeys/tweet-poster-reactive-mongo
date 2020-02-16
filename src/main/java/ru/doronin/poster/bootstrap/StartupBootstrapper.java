package ru.doronin.poster.bootstrap;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.doronin.poster.post.Tweet;
import ru.doronin.poster.post.TweetService;
import ru.doronin.poster.user.SystemUser;
import ru.doronin.poster.user.UserService;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.stream.Stream;

/**
 * Component required to fill the DB at startup time
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StartupBootstrapper implements ApplicationListener<ContextRefreshedEvent> {
    private final static SecureRandom SECURE_RANDOM = new SecureRandom();
    private final static Faker FAKER_INSTANCE = new Faker(SECURE_RANDOM);
    private final TweetService tweetService;
    private final UserService userService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        long users = userService.countRecords(),
                tweets = tweetService.countRecords();
        log.info(users == 0 ? "There are no users in the DB" : String.format("There are %d users in the DB", users));
        log.info(tweets == 0 ? "There are no tweets in the DB" : String.format("There are %d tweets in the DB", tweets));
        if (users == 0 && tweets == 0) {
            SystemUser admin = SystemUser.builder()
                    .login("admin")
                    .email(FAKER_INSTANCE.internet().emailAddress())
                    .password("password")
                    .build();
            userService.save(admin).subscribe(adminUser -> log.info("Saved admin"));
            Flux<Tweet> data = Flux.fromStream(Stream.iterate(0, integer -> integer++).limit(5))
                    .flatMap(i -> generateUserData())
                    .flatMap(this::generateTweets);
            data.subscribe(tweet -> log.info("Saved tweet"));
        }
    }

    private Flux<Tweet> generateTweets(SystemUser user) {
        return Flux.fromStream(Stream.iterate(0, i -> i++)
                .limit(SECURE_RANDOM.nextInt(9) + 1))
                .flatMap(i -> generateTweet(user, i));
    }

    private Mono<Tweet> generateTweet(SystemUser user, int salt) {
        Instant moment = Instant.now().minusSeconds(salt * 60 + SECURE_RANDOM.nextInt(40));
        Tweet tweet = Tweet.builder()
                .author(user)
                .content(FAKER_INSTANCE.gameOfThrones().quote())
                .created(moment)
                .modified(moment)
                .build();
        return tweetService.save(tweet);
    }

    private Mono<SystemUser> generateUserData() {
        SystemUser user = SystemUser.builder()
                .login(FAKER_INSTANCE.gameOfThrones().character().replaceAll("\\s+", "_"))
                .email(FAKER_INSTANCE.internet().safeEmailAddress())
                .password(RandomStringUtils.randomAlphanumeric(16))
                .build();
        log.info(String.format("Saving user with login: %s and password: %s", user.getLogin(), user.getPassword()));
        return userService.save(user);
    }
}
