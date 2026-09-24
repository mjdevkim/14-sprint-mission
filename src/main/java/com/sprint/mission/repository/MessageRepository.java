package com.sprint.mission.repository;

import com.sprint.mission.domain.Message;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @EntityGraph(attributePaths = {"author", "author.profile", "author.status"})
    List<Message> findAllByChannelId(UUID channelId);

    Optional<Message> findFirstByChannelIdOrderByCreatedAtDesc(UUID channelId);

    default Message getMessage(UUID messageId) {
        if (Objects.isNull(messageId)) {
            throw new DiscodeitException(DiscodeitExceptionType.MESSAGE_ID_IS_NULL);
        }

        return findById(messageId)
                .orElseThrow(() -> new DiscodeitException(DiscodeitExceptionType.MESSAGE_NOT_FOUND, messageId));
    }
}
