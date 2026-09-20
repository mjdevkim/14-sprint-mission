package com.sprint.mission.repository.file;

import com.sprint.mission.domain.BinaryContent;
import com.sprint.mission.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository
        extends AbstractFileRepository<BinaryContent>
        implements BinaryContentRepository {

    private static final String BINARY_CONTENT_FILENAME = "binary-contents.ser";

    private final Map<UUID, BinaryContent> binaryContentMap;

    public FileBinaryContentRepository(
            // 1. application.yaml의 값을 전달 받아서 String fileDirectory에 넣는다
            @Value("${discodeit.repository.file-directory:.discodeit/objects}")
            String fileDirectory
    ) {
        super("BinaryContent", fileDirectory, BINARY_CONTENT_FILENAME);
        this.binaryContentMap = loadFile();
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        binaryContentMap.put(binaryContent.getId(), binaryContent);
        saveFile(binaryContentMap);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID binaryContentId) {
        return Optional.ofNullable(binaryContentMap.get(binaryContentId));
    }

    @Override
    public List<BinaryContent> findAll() {
        return binaryContentMap.values().stream().toList();
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = new ArrayList<>();

        for (UUID binaryContentId : binaryContentIds) {
            binaryContents.add(binaryContentMap.get(binaryContentId));
        }

        return binaryContents;
    }

    @Override
    public void delete(UUID binaryContentId) {
        binaryContentMap.remove(binaryContentId);
        saveFile(binaryContentMap);
    }

}
