package com.sprint.mission.repository;

import com.sprint.mission.domain.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    Optional<ReadStatus> findById(UUID readStatusId);
    List<ReadStatus> findAllByChannelId(UUID channelId);
    List<ReadStatus> findAllByUserId(UUID userId);
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);
    List<ReadStatus> findAll();
    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);
    void delete(UUID readStatusId);
    void deleteAllByChannelId(UUID channelId);
}
