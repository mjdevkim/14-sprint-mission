package com.sprint.mission.repository;

import com.sprint.mission.domain.Channel;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Objects;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    default Channel getChannel(UUID channelId) {
        if (Objects.isNull(channelId)) {
            throw new DiscodeitException(DiscodeitExceptionType.CHANNEL_ID_IS_NULL);
        }

        return findById(channelId)
                .orElseThrow(() -> new DiscodeitException(DiscodeitExceptionType.CHANNEL_NOT_FOUND, channelId));
    }
}
