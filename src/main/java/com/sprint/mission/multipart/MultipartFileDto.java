package com.sprint.mission.multipart;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MultipartFileDto {

    private final String fileName;
    private final String contentType;
    private final byte[] bytes;
}
