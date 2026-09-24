package com.sprint.mission.repository;

import com.sprint.mission.domain.ReadStatus;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    @EntityGraph(attributePaths = {"user", "user.profile", "user.status"})
    List<ReadStatus> findAllByChannelId(UUID channelId);

    List<ReadStatus> findAllByUserId(UUID userId);
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);
    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    default ReadStatus getReadStatus(UUID readStatusId) {
        if (Objects.isNull(readStatusId)) {
            throw new DiscodeitException(DiscodeitExceptionType.READ_STATUS_ID_IS_NULL);
        }

        return findById(readStatusId)
                .orElseThrow(() -> new DiscodeitException(DiscodeitExceptionType.READ_STATUS_NOT_FOUND, readStatusId));
    }

    default ReadStatus createReadStatus(ReadStatus readStatus) {
        if (existsByUserIdAndChannelId(readStatus.getUser().getId(), readStatus.getChannel().getId())) {
            throw new DiscodeitException(
                    DiscodeitExceptionType.READ_STATUS_ALREADY_EXISTS,
                    readStatus.getUser().getId(),
                    readStatus.getChannel().getId()
            );
        }
        return save(readStatus);
    }
}
