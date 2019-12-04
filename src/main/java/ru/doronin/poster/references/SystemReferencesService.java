package ru.doronin.poster.references;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SystemReferencesService {
    @Value("${spring.profiles.active}")
    private String profiles;

    public boolean isDemo() {
        return profiles.contains("demo");
    }
}
