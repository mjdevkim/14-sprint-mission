package com.sprint.mission.multipart;

import com.sprint.mission.exception.DiscodeitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public abstract class AbstractMultipartFileConverter implements MultipartFileConverter {

    @Override
    public MultipartFileDto convert(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new DiscodeitException(MultipartFileExceptionType.FILE_IS_EMPTY);
        }

        String fileName = generate(file);

        try {
            return new MultipartFileDto(
                    fileName,
                    file.getContentType(),
                    file.getBytes()
            );
        } catch (IOException exception) {
            log.error(
                    "MultipartFile 변환 중 오류가 발생했습니다. fileName={}",
                    fileName,
                    exception
            );
            throw new DiscodeitException(MultipartFileExceptionType.MULTIPART_FILE_READ_FAILED);
        }
    }

    protected abstract String generate(MultipartFile file);
}
