package com.sprint.mission.domain;

import com.sprint.mission.domain.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Duration;
import java.time.Instant;

import static lombok.AccessLevel.PROTECTED;

// 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델
// 사용자의 온라인 상태를 확인하기 위해 활용합니다.
@Entity
@Table(name = "user_statuses")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class UserStatus extends BaseUpdatableEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;              // 어떤 사용자인지 나타내는 User

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt;

    private UserStatus(User user) {
        this.user = user;
        this.lastActiveAt = Instant.now();
    }

    public static UserStatus create(User user) {
        return new UserStatus(user);
    }

    public void updateLastActiveAt(Instant newLastActiveAt) {
        this.lastActiveAt = newLastActiveAt;
    }

    public boolean isOnline() {
        return Duration.between(lastActiveAt, Instant.now()).toMinutes() < 5;
    }
}
