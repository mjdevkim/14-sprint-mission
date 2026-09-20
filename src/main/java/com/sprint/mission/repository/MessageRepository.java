package com.sprint.mission.repository;

import com.sprint.mission.domain.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID messageId);
    Optional<Message> findMostRecentByChannelId(UUID channelId);
    List<Message> findAll();
    void delete(UUID messageId);
    void deleteAllByChannelId(UUID channelId);
}
