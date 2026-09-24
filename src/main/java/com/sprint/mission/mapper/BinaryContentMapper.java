package com.sprint.mission.mapper;

import com.sprint.mission.controller.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.domain.BinaryContent;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BinaryContentMapper {

    public BinaryContentDto toDto(BinaryContent binaryContent) {
        if (Objects.isNull(binaryContent)) {
            return null;
        }

        return BinaryContentDto.from(binaryContent);
    }
}
