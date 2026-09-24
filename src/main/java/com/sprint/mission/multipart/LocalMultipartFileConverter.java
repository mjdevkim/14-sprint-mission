package com.sprint.mission.multipart;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Component
public class LocalMultipartFileConverter extends AbstractMultipartFileConverter {

    @Override
    protected String generate(MultipartFile file) {
        // 디스크에 저장할 때 실제 키는 BinaryContent의 id(UUID)가 맡는다.
        // 여기서 만드는 fileName은 다운로드 시 보여줄 이름일 뿐이라 UUID를 덧붙일 필요가 없다.
        return sanitize(file.getOriginalFilename());
    }

    private String sanitize(String originalFileName) {
        if (Objects.isNull(originalFileName) || originalFileName.isBlank()) {
            originalFileName = "unknown";
        }

        int lastWinSep = originalFileName.lastIndexOf("\\");
        int lastUnixSep = originalFileName.lastIndexOf("/");
        int lastIndex = Math.max(lastWinSep, lastUnixSep);

        String sanitized = (lastIndex != -1)
                ? originalFileName.substring(lastIndex + 1)
                : originalFileName;
        return sanitized.isBlank() ? "unknown" : sanitized;
    }
}
