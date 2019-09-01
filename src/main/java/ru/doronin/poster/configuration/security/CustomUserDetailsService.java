package ru.doronin.poster.configuration.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.doronin.poster.user.UserService;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements ReactiveUserDetailsService {
    private final static String ADMIN_LOGIN = "admin";
    private final UserService userService;

    /**
     * Find the {@link UserDetails} by username.
     *
     * @param username the username to look up
     * @return the {@link UserDetails}. Cannot be null
     */
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userService.loadByLogin(username)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException("Unable to find this user"))))
                .map(user -> {
                    String[] roles = ADMIN_LOGIN.equals(username) ? new String[]{"USER", "ADMIN"} : new String[]{"USER"};
                    return User.builder().username(username).password(user.getPassword())
                            .roles(roles)
                            .accountExpired(false)
                            .accountLocked(false)
                            .disabled(false)
                            .credentialsExpired(false)
                            .build();
                });
    }
}
