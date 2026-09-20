package com.sprint.mission.application.user.provided.query;

import com.sprint.mission.application.user.dto.UserResponseDto;

import java.util.UUID;

public interface UserFinder {
    UserResponseDto getById(UUID userId);
}
