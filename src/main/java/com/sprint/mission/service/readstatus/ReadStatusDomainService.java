package com.sprint.mission.service.readstatus;

import com.sprint.mission.domain.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusDomainService {
    ReadStatus create(ReadStatus readStatus);
    List<ReadStatus> createAll(List<ReadStatus> readStatuses);
    ReadStatus findById(UUID readStatusId);
    List<ReadStatus> findAll();
    List<ReadStatus> findAllByChannelId(UUID channelId);
    List<ReadStatus> findAllByUserId(UUID userId);
    ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId);
    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);
    ReadStatus update(ReadStatus updatingReadStatus);
    void delete(UUID readStatusId);
    void deleteAllByChannelId(UUID channelId);
}
