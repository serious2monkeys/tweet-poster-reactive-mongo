package ru.doronin.poster.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<SystemUser> loadById(String id) {
        return userRepository.findById(id);
    }

    public Mono<SystemUser> loadByLogin(String login) {
        return userRepository.findFirstByLogin(login);
    }

    public Mono<SystemUser> save(SystemUser user) {
        Instant moment = Instant.now();
        if (user.getCreated() == null) {
            user.setCreated(moment);
        }
        user.setModified(moment);
        user.setPassword(encodePassword(user));
        return userRepository.save(user);
    }

    private String encodePassword(SystemUser user) {
        if (user.getId() == null) {
            return user.getPassword() == null ? null : passwordEncoder.encode(user.getPassword());
        }

        Optional<SystemUser> storedUser = loadById(user.getId()).blockOptional();
        if (user.getPassword() == null) {
            return storedUser.get().getPassword();
        }

        if (!storedUser.get().getPassword().equals(user.getPassword())) {
            return passwordEncoder.encode(user.getPassword());
        }
        return user.getPassword();
    }

    public Flux<SystemUser> loadAll() {
        return userRepository.findAll();
    }

    public Long countRecords() {
        return userRepository.count().block();
    }

    public Mono<SystemUser> getCurrent() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(this::loadByLogin);
    }
}
