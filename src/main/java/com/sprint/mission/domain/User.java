package com.sprint.mission.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

     private UUID profileId;    // 프로필 이미지인 BinaryContent의 ID

    private String username;
    private String email;
    private String password;

    // UserRequestDto 통해서 User 객체를 생성할것이기 때문에 private으로
    private User(
            String username,
            String email,
            String password,
            UUID profileId
    ) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;

        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }


    // 이 정적 메소드를 통해서 User 객체 생성
    public static User create(
            String username,
            String email,
            String password,
            UUID profileId
    ) {
        return new User(
                username,
                email,
                password,
                profileId
        );
    }


    public void updateAccountDetails(
            String username,
            String email,
            String password,
            UUID profileId
    ) {
        boolean isUpdated = false;

        if (Objects.nonNull(username)
                && !Objects.equals(username, this.username)) {
            this.username = username;
            isUpdated = true;
        }

        if (Objects.nonNull(email)
                && !Objects.equals(email, this.email)) {
            this.email = email;
            isUpdated = true;
        }

        if (Objects.nonNull(password)
                && !Objects.equals(password, this.password)) {
            this.password = password;
            isUpdated = true;
        }

        if (Objects.nonNull(profileId)
                && !Objects.equals(profileId, this.profileId)) {
            this.profileId = profileId;
            isUpdated = true;
        }

        if (isUpdated) {
            this.updatedAt = Instant.now();
        }
    }


    public User copy() {
        return new User(
                this.id,
                this.createdAt,
                this.updatedAt,
                this.profileId,
                this.username,
                this.email,
                this.password
        );
    }

    public boolean matchesPassword(String password) {
        return Objects.equals(this.password, password);
    }
}
