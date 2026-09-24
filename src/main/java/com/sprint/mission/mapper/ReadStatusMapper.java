package com.sprint.mission.mapper;

import com.sprint.mission.controller.dto.readstatus.ReadStatusDto;
import com.sprint.mission.domain.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

    public ReadStatusDto toDto(ReadStatus readStatus) {
        return ReadStatusDto.from(readStatus);
    }
}
