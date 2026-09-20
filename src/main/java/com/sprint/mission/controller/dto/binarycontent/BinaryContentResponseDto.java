package com.sprint.mission.controller.dto.binarycontent;

import com.sprint.mission.domain.BinaryContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Schema(name = "BinaryContent")
public class BinaryContentResponseDto {
    UUID id;
    Instant createdAt;
    String fileName;
    long size;
    String contentType;
    byte[] bytes;

    public static BinaryContentResponseDto from(BinaryContent binaryContent) {
        return new BinaryContentResponseDto(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFileName(),
                binaryContent.getBytes().length,
                binaryContent.getContentType(),
                binaryContent.getBytes()
        );
    }
}
