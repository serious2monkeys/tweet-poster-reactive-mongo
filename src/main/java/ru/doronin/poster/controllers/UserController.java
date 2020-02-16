package ru.doronin.poster.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.doronin.poster.user.SystemUser;
import ru.doronin.poster.user.UserService;

import java.util.Collections;

@Profile("controllers")
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SystemUser> introduce() {
        return userService.getCurrent();
    }

    @GetMapping(value = "/user/{login}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> getUserInfo(@PathVariable("login") String login) {
        if (login == null) {
            return Mono.just(new ResponseEntity<>(Collections.singletonMap("message", "User not specified"),
                    HttpStatus.BAD_REQUEST));
        }
        return userService.loadByLogin(login)
                .map(user -> new ResponseEntity<Object>(user, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(Collections.singletonMap("message", "User not found"),
                        HttpStatus.BAD_REQUEST));
    }

    @PostMapping(value = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> signUp(@RequestBody SystemUser user) {
        if (StringUtils.isEmpty(user) || StringUtils.isEmpty(user.getLogin()) || StringUtils.isEmpty(user.getPassword())) {
            return Mono.just(new ResponseEntity<>(Collections.singletonMap("message", "User credentials not specified"),
                    HttpStatus.BAD_REQUEST));
        }
        return userService.loadByLogin(user.getLogin())
                .flatMap(found -> Mono.just(new ResponseEntity<Object>(Collections.singletonMap("message", "User with such login already exists"),
                        HttpStatus.BAD_REQUEST)))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(userService.save(user), HttpStatus.CREATED)));
    }
}
