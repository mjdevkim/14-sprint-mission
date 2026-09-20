package com.sprint.mission.repository.jcf;

import com.sprint.mission.domain.ReadStatus;
import com.sprint.mission.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFReadStatusRepository implements ReadStatusRepository {

    private static final Map<UUID, ReadStatus> readStatusMap = new HashMap<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        readStatusMap.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        return Optional.ofNullable(readStatusMap.get(readStatusId));
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return readStatusMap.values().stream()
                .filter(readStatus ->
                        Objects.equals(readStatus.getChannelId(), channelId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusMap.values().stream()
                .filter(readStatus ->
                        Objects.equals(readStatus.getUserId(), userId))
                .toList();
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatusMap.values().stream()
                .filter(readStatus -> Objects.equals(readStatus.getUserId(), userId))
                .filter(readStatus -> Objects.equals(readStatus.getChannelId(), channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAll() {
        return readStatusMap.values().stream().toList();
    }

    @Override
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        for (ReadStatus readStatus : readStatusMap.values()) {
            if (Objects.equals(readStatus.getUserId(), userId) &&
                    Objects.equals(readStatus.getChannelId(), channelId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void delete(UUID readStatusId) {
        readStatusMap.remove(readStatusId);
    }

    // channel을 지울때, 그 채널에 기록됐던 read status도 지우기 위해 필요한 메소드
    @Override
    public void deleteAllByChannelId(UUID channelId) {
        List<UUID> readStatusIdsToDelete = new ArrayList<>();

        // 한 채널에 대한 모든 read status를 모은다
        for (ReadStatus readStatus : readStatusMap.values()) {
            if (Objects.equals(readStatus.getChannelId(), channelId)) {
                readStatusIdsToDelete.add(readStatus.getId());
            }
        }

        // 삭제
        for (UUID readStatusId : readStatusIdsToDelete) {
            readStatusMap.remove(readStatusId);
        }
    }
}
