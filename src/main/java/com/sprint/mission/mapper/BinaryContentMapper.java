package com.sprint.mission.mapper;

import com.sprint.mission.controller.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.storage.BinaryContentStorage;
import com.sprint.mission.domain.BinaryContent;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BinaryContentMapper {
    private final BinaryContentStorage binaryContentStorage;

    public BinaryContentDto toDto(BinaryContent binaryContent) {
        if (Objects.isNull(binaryContent)) {
            return null;
        }

        byte[] bytes = readBytes(binaryContent.getId());
        return BinaryContentDto.from(binaryContent, bytes);
    }

    private byte[] readBytes(UUID binaryContentId) {
        try (InputStream inputStream = binaryContentStorage.get(binaryContentId)) {
            return inputStream.readAllBytes();
        } catch (IOException exception) {
            throw new DiscodeitException(
                    DiscodeitExceptionType.BINARY_CONTENT_STORAGE_READ_FAILED,
                    binaryContentId
            );
        }
    }
}
