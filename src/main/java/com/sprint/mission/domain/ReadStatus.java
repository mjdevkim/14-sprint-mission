package com.sprint.mission.domain;

import com.sprint.mission.domain.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

// 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용.
@Entity
@Table(
        name = "read_statuses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "channel_id"})
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ReadStatus extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)    // ReadStatus N : User 1
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)    // ReadStatus N : Channel 1
    @JoinColumn(name = "channel_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Channel channel;

    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt;

    private ReadStatus(User user, Channel channel, Instant lastReadAt) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = lastReadAt;
    }

    public static ReadStatus create(User user, Channel channel) {
        return new ReadStatus(user, channel, Instant.now());
    }

    public static ReadStatus create(User user, Channel channel, Instant lastReadAt) {
        return new ReadStatus(
                user,
                channel,
                Objects.isNull(lastReadAt) ? Instant.now() : lastReadAt
        );
    }

    public void updateLastReadAt(Instant newLastReadAt) {
        this.lastReadAt = newLastReadAt;
    }
}
