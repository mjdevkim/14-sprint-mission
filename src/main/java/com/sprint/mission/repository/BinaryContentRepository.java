package com.sprint.mission.repository;

import com.sprint.mission.domain.BinaryContent;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {
    List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds);

    default BinaryContent getBinaryContent(UUID binaryContentId) {
        if (Objects.isNull(binaryContentId)) {
            throw new DiscodeitException(DiscodeitExceptionType.BINARY_CONTENT_ID_IS_NULL);
        }

        return findById(binaryContentId)
                .orElseThrow(() ->
                        new DiscodeitException(DiscodeitExceptionType.BINARY_CONTENT_NOT_FOUND, binaryContentId));
    }
}
