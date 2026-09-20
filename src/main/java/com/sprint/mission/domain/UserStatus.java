package com.sprint.mission.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

// 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델
// 사용자의 온라인 상태를 확인하기 위해 활용합니다.
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;                  // UserStatus 자체의 ID
    private final Instant createdAt;
    private Instant updatedAt;

    private final UUID userId;              // 어떤 사용자인지 나타내는 ID
    private Instant lastActiveAt;

    private UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.userId = userId;
        this.lastActiveAt = this.createdAt;
    }

    public static UserStatus create(UUID userId) {
        return new UserStatus(userId);
    }

    public void updateLastActiveAt(Instant newLastActiveAt) {
        this.lastActiveAt = newLastActiveAt;
        this.updatedAt = Instant.now();
    }

    public boolean isOnline() {
        return Duration.between(lastActiveAt, Instant.now()).toMinutes() < 5;
    }
}
