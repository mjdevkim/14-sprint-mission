package com.sprint.mission.storage;

import com.sprint.mission.controller.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(
            @Value("${discodeit.storage.local.root-path:uploads}") String rootPath
    ) {
        this.root = Path.of(rootPath).toAbsolutePath().normalize();
        init(); // 디렉토리 init
    }

    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException exception) {
            log.error("파일 저장 디렉터리 생성 실패. root={}", root, exception);
            throw new DiscodeitException(DiscodeitExceptionType.BINARY_CONTENT_STORAGE_WRITE_FAILED);
        }
    }

    @Override
    public UUID put(UUID key, byte[] bytes) {
        Path path = resolvePath(key);
        try {
            Files.write(path, bytes);
        } catch (IOException exception) {
            log.error("파일 저장 실패. key={}", key, exception);
            throw new DiscodeitException(DiscodeitExceptionType.BINARY_CONTENT_STORAGE_WRITE_FAILED, key);
        }
        return key;
    }

    @Override
    public InputStream get(UUID key) {
        Path path = resolvePath(key);
        try {
            return Files.newInputStream(path);  // 파일을 읽는 통로?를 연다
                // BinaryContentMapper가 InputStream을 .readAllBytes()로 읽어감
        } catch (IOException exception) {
            log.error("파일 읽기 실패. key={}", key, exception);
            throw new DiscodeitException(DiscodeitExceptionType.BINARY_CONTENT_STORAGE_READ_FAILED, key);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        InputStreamResource resource = new InputStreamResource(get(binaryContentDto.getId()));

        MediaType contentType = binaryContentDto.getContentType() == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(binaryContentDto.getContentType());

        return ResponseEntity.ok()
                .contentType(contentType)
                // content disposition 헤더면 파일 다운로드 창을 띄움
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(binaryContentDto.getFileName(), StandardCharsets.UTF_8)   // db 저장된 파일명
                        .build().toString())
                .body(resource);
    }

    private Path resolvePath(UUID key) {
        return root.resolve(key.toString());
    }
}
