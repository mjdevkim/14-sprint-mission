package com.sprint.mission.controller.dto.binarycontent;

import com.sprint.mission.domain.BinaryContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Schema(name = "BinaryContentDto")
public class BinaryContentDto {
    UUID id;
    String fileName;
    long size;
    String contentType;
    byte[] bytes;

    public static BinaryContentDto from(BinaryContent binaryContent) {
        return new BinaryContentDto(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getSize(),
                binaryContent.getContentType(),
                binaryContent.getBytes()
        );
    }

    public byte[] getBytes() {
        return bytes.clone();
    }
}
