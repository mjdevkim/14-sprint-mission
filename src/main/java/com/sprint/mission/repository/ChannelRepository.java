package com.sprint.mission.repository;

import com.sprint.mission.domain.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);
    Optional<Channel> findById(UUID channelId);
    List<Channel> findAll();
    void delete(UUID channelId);
}
