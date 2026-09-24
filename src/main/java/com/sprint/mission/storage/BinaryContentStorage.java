package com.sprint.mission.storage;

import com.sprint.mission.controller.dto.binarycontent.BinaryContentDto;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;

// 실제 파일 관련 operation
public interface BinaryContentStorage {

    UUID put(UUID key, byte[] bytes);
    InputStream get(UUID key);
    ResponseEntity<?> download(BinaryContentDto binaryContentDto);
}
