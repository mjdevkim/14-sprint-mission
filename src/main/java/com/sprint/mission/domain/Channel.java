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
public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private String name;
    private String description;
    private ChannelType channelType;

    // ChannelCreateDto를 통해 Channel 객체 생성, 따라서 private으로
    private Channel(
            String name,
            String description,
            ChannelType channelType) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;

        this.name = name;
        this.description = description;
        this.channelType = channelType;
    }

    // Public Channel 객체를 생성하는 정적 팩토리 메소드
    public static Channel createPublic(String name, String description) {
        return new Channel(
                name,
                description,
                ChannelType.PUBLIC
        );
    }

    // Public Channel 객체를 생성하는 정적 팩토리 메소드
    public static Channel createPrivate() {
        return new Channel(
                null,
                null,
                ChannelType.PRIVATE
        );
    }

    public void updateNameAndDescription(
            String name,
            String description
    ) {
        boolean isUpdated = false;

        if (Objects.nonNull(name) &&
            !Objects.equals(name, this.name)
        ) {
            isUpdated = true;
            this.name = name;
        }

        if (Objects.nonNull(description) &&
            !Objects.equals(description, this.description)
        ) {
            isUpdated = true;
            this.description = description;
        }

        if (isUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public Channel copy() {
        return new Channel(
                this.id,
                this.createdAt,
                this.updatedAt,
                this.name,
                this.description,
                this.channelType
        );
    }
}
