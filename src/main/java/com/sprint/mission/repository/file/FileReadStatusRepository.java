package com.sprint.mission.repository.file;

import com.sprint.mission.domain.ReadStatus;
import com.sprint.mission.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileReadStatusRepository
        extends AbstractFileRepository<ReadStatus>
        implements ReadStatusRepository {

    private static final String READ_STATUS_FILENAME = "read-statuses.ser";

    private final Map<UUID, ReadStatus> readStatusMap;

    public FileReadStatusRepository(
            @Value("${discodeit.repository.file-directory:.discodeit/objects}")
            String fileDirectory
    ) {
        super("ReadStatus", fileDirectory, READ_STATUS_FILENAME);
        this.readStatusMap = loadFile();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        readStatusMap.put(readStatus.getId(), readStatus);
        saveFile(readStatusMap);
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
                .filter(readStatus -> Objects.equals(readStatus.getUserId(), userId))
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

    // 해당 user id와 channel id에 이미 read status가 존재하는지 확인한다
    @Override
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        for (ReadStatus readStatus : readStatusMap.values()) {
            if (Objects.equals(readStatus.getUserId(), userId)
                    && Objects.equals(readStatus.getChannelId(), channelId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void delete(UUID readStatusId) {
        readStatusMap.remove(readStatusId);
        saveFile(readStatusMap);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        List<ReadStatus> readStatusesToDelete = new ArrayList<>();

        // channelId의 모든 read status들을 한 곳에 모은다
        for (ReadStatus readStatus : readStatusMap.values()) {
            if (Objects.equals(readStatus.getChannelId(), channelId)) {
                readStatusesToDelete.add(readStatus);
            }
        }

        // 그 read status들을 한 곳에 모은다
        for (ReadStatus readStatus : readStatusesToDelete) {
            readStatusMap.remove(readStatus.getId());
        }

        saveFile(readStatusMap);
    }

}
