package com.sprint.mission.repository;

import com.sprint.mission.domain.UserStatus;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {
    Optional<UserStatus> findByUserId(UUID userId);

    default UserStatus getUserStatusByUserId(UUID userId) {
        if (Objects.isNull(userId)) {
            throw new DiscodeitException(DiscodeitExceptionType.USER_ID_IS_NULL);
        }

        return findByUserId(userId)
                .orElseThrow(() -> new DiscodeitException(DiscodeitExceptionType.USER_STATUS_NOT_FOUND, userId));
    }
}
