package com.sprint.mission.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Getter
public class BinaryContent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;

    private final String fileName;
    private final String contentType;
    private final byte[] bytes;


    private BinaryContent(String fileName, String contentType, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
    }

    public static BinaryContent create(String fileName, String contentType, byte[] fileBytes) {
        return new BinaryContent(fileName, contentType, fileBytes);
    }

//    public static BinaryContent create(MultipartFile multipartFile) {
//        BinaryContent newBinaryContent = null;
//
//        try {
//            newBinaryContent = new BinaryContent(
//                    multipartFile.getOriginalFilename(),
//                    multipartFile.getBytes()
//            );
//        } catch (Exception e) {
//            log.warn("엥? Binary Content 변환 안됨");
//        }
//
//        return newBinaryContent;
//    }

    public byte[] getBytes() {
        return this.bytes.clone();
    }
}
