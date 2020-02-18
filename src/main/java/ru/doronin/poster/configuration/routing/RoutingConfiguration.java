package ru.doronin.poster.configuration.routing;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.doronin.poster.post.Tweet;
import ru.doronin.poster.post.TweetService;
import ru.doronin.poster.references.SystemReferencesService;
import ru.doronin.poster.user.SystemUser;
import ru.doronin.poster.user.UserService;

import java.util.Collections;

/**
 * Substitution for classic "controller" way
 */
@Profile("functional")
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RoutingConfiguration {
    private final TweetService tweetService;
    private final UserService userService;
    private final SystemReferencesService systemReferencesService;

    /**
     * Functional way to route request processing. Awesome, isn't it?
     */
    @Bean("routerFunction")
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route()
                .GET("/tweets", request -> handleAllTweetsRequest())
                .GET("/login", this::renderLoginPage)
                .GET("/index", this::renderIndexPage)
                .GET("/", this::renderIndexPage)
                .POST("/tweets", this::handlePublishTweet)
                .PUT("/tweets", this::handleUpdateTweet)
                .DELETE("/tweets/{id}", this::handleDeleteTweet)
                .GET("/me", request -> introduce())
                .POST("/signup", this::handleSignUp)
                .build();
    }

    private Mono<ServerResponse> renderLoginPage(ServerRequest request) {
        return ServerResponse.ok().render("login",
                Collections.singletonMap("isDemo", systemReferencesService.isDemo()));
    }

    private Mono<ServerResponse> renderIndexPage(ServerRequest request) {
        return ServerResponse.ok().render("index",
                Collections.singletonMap("isDemo", systemReferencesService.isDemo()));
    }

    private Mono<ServerResponse> handleUpdateTweet(ServerRequest request) {
        return request.bodyToMono(Tweet.class).flatMap(tweet -> {
            if (tweet.getId() == null) {
                return ServerResponse.badRequest()
                        .bodyValue(Collections.singletonMap("message", "Message identifier not specified"));
            } else {
                return userService.getCurrent().flatMap(user -> tweetService.load(tweet.getId())
                        .flatMap(foundTweet -> {
                            if (foundTweet.getAuthor().getLogin().equals(user.getLogin())) {
                                foundTweet.setContent(tweet.getContent());
                                return ServerResponse.ok().body(tweetService.save(foundTweet), Tweet.class);
                            } else {
                                return ServerResponse.status(HttpStatus.FORBIDDEN)
                                        .bodyValue(Collections.singletonMap("message", "Access denied"));
                            }
                        })
                        .switchIfEmpty(ServerResponse.badRequest()
                                .bodyValue(Collections.singletonMap("message", "Message not found"))));
            }
        });
    }

    private Mono<ServerResponse> handleDeleteTweet(ServerRequest request) {
        if (request.pathVariables().containsKey("id")) {
            return tweetService.load(request.pathVariable("id")).flatMap(tweet ->
                    userService.getCurrent().flatMap(user -> {
                        if (user.getLogin().equals(tweet.getAuthor().getLogin())) {
                            return tweetService.delete(tweet)
                                    .then(ServerResponse.ok().bodyValue(Collections.singletonMap("message", "Tweet deleted")));
                        } else {
                            return ServerResponse.status(HttpStatus.FORBIDDEN).bodyValue(Collections.singletonMap("message", "Access denied"));
                        }
                    })
            ).switchIfEmpty(ServerResponse.badRequest().bodyValue(Collections.singletonMap("message", "Tweet not found")));
        }
        return ServerResponse.badRequest().bodyValue(Collections.singletonMap("message", "Tweet not specified"));
    }

    private Mono<ServerResponse> handleSignUp(ServerRequest request) {
        return request.bodyToMono(SystemUser.class).flatMap(user -> {
            if (StringUtils.isEmpty(user)
                    || StringUtils.isEmpty(user.getLogin())
                    || StringUtils.isEmpty(user.getPassword())) {
                return ServerResponse.badRequest()
                        .bodyValue(Collections.singletonMap("message", "User credentials not specified"));
            } else {
                return userService.loadByLogin(user.getLogin())
                        .flatMap(found ->
                                ServerResponse.badRequest().bodyValue(Collections.singletonMap("message", "User with such login already exists")))
                        .switchIfEmpty(Mono.defer(() ->
                                ServerResponse.ok().body(userService.save(user), SystemUser.class)));
            }
        });
    }

    private Mono<ServerResponse> introduce() {
        return ServerResponse.ok().body(userService.getCurrent(), SystemUser.class);
    }

    private Mono<ServerResponse> handlePublishTweet(ServerRequest request) {
        return request.bodyToMono(JsonNode.class).flatMap(node -> {
            if (!node.has("tweet")) {
                return ServerResponse.badRequest().bodyValue(Collections.singletonMap("message", "Text not specified"));
            }
            return ServerResponse.ok().body(tweetService.create(node.get("tweet")
                    .asText(), userService.getCurrent()), Tweet.class);
        });
    }

    private Mono<ServerResponse> handleAllTweetsRequest() {
        return ServerResponse.ok().body(tweetService.loadAll(), Tweet.class);
    }
}
