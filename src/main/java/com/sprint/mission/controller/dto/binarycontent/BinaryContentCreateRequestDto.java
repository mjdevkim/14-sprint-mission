package com.sprint.mission.controller.dto.binarycontent;

import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;


@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BinaryContentCreateRequestDto {

    @NotBlank
    String fileName;

    @NotEmpty
    byte[] bytes;

    public static BinaryContentCreateRequestDto of(
            String fileName,
            byte[] bytes
    ) {
        // 모든 DTO에 있는 Null/ blank 검증은 다음 미션에서
        // Controller에 @Valid validator를 활용해서 @NotBlank 등으로 처리할 예정
        if (Objects.isNull(fileName) || fileName.isBlank()) {
            throw new DiscodeitException(DiscodeitExceptionType.BINARY_CONTENT_FILE_NAME_IS_NULL);
        }

        if (Objects.isNull(bytes)) {
            throw new DiscodeitException(DiscodeitExceptionType.BINARY_CONTENT_BYTES_IS_NULL);
        }

        return new BinaryContentCreateRequestDto(fileName, bytes);
    }

    public byte[] getBytes() {
        return bytes.clone();
    }
}
