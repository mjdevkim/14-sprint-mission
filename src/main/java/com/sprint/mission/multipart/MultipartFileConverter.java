package com.sprint.mission.multipart;

import org.springframework.web.multipart.MultipartFile;

public interface MultipartFileConverter {
    MultipartFileDto convert(MultipartFile file);
}
