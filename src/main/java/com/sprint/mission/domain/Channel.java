package com.sprint.mission.domain;

import com.sprint.mission.domain.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "channels")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Channel extends BaseUpdatableEntity {

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private ChannelType type;

    // ChannelCreateDto를 통해 Channel 객체 생성, 따라서 private으로
    private Channel(
            String name,
            String description,
            ChannelType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    // Public Channel 객체를 생성하는 정적 팩토리 메소드
    public static Channel createPublic(String name, String description) {
        return new Channel(
                name,
                description,
                ChannelType.PUBLIC
        );
    }

    // Private Channel 객체를 생성하는 정적 팩토리 메소드
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
        if (Objects.nonNull(name) &&
            !Objects.equals(name, this.name)
        ) {
            this.name = name;
        }

        if (Objects.nonNull(description) &&
            !Objects.equals(description, this.description)
        ) {
            this.description = description;
        }
    }
}
