package com.sprint.mission.repository.jcf;

import com.sprint.mission.domain.BinaryContent;
import com.sprint.mission.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private static final Map<UUID, BinaryContent> binaryContentMap = new HashMap<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        binaryContentMap.put(binaryContent.getId(), binaryContent);
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

    // Binary content id를 list로 주면 그 id를 가진 binary content를 반환한다.
    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentIds.stream()
                .map(binaryContentMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void delete(UUID binaryContentId) {
        binaryContentMap.remove(binaryContentId);
    }
}
