package ru.doronin.poster.user;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Простая сущность пользователя системы
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonFilter("ignoringPassword")
public class SystemUser {
    @Id
    private String id;

    @NotBlank
    private String login;

    @Email
    private String email;

    @NotBlank
    private String password;

    @CreatedDate
    @NotNull
    private Instant created;

    @LastModifiedDate
    @NotNull
    private Instant modified;
}
