package com.sprint.mission.service.binarycontent;

import com.sprint.mission.domain.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentDomainService {
    BinaryContent create(BinaryContent binaryContent);

    BinaryContent findById(UUID binaryContentId);

    List<BinaryContent> findAll();

    List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds);

    void delete(UUID binaryContentId);
}
