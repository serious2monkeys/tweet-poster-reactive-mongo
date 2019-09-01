package ru.doronin.poster.post;

import io.github.kaiso.relmongo.annotation.FetchType;
import io.github.kaiso.relmongo.annotation.ManyToOne;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.doronin.poster.user.SystemUser;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "tweets")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tweet {
    @Id
    private String id;

    @NotBlank
    @Size(max = 140)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private SystemUser author;

    @CreatedDate
    @NotNull
    private Instant created;

    @LastModifiedDate
    @NotNull
    private Instant modified;
}
