package com.sprint.mission.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

// 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용.
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private final UUID userId;
    private final UUID channelId;

    private Instant lastReadAt;

    private ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public static ReadStatus create(UUID userId, UUID channelId) {
        return new ReadStatus(
                userId, channelId, null
        );
    }

    public static ReadStatus create(UUID userId, UUID channelId, Instant lastReadAt) {
        return new ReadStatus(userId, channelId, lastReadAt);
    }


    public void updateLastReadAt(Instant newLastReadAt) {
        this.lastReadAt = newLastReadAt;
        this.updatedAt = Instant.now();
    }
}
