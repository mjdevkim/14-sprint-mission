package com.sprint.mission.application.user.provided.command;

import com.sprint.mission.application.user.dto.UserResponseDto;
import com.sprint.mission.application.user.dto.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserModifier {
    UserResponseDto update(UUID userId, UserUpdateRequest request, MultipartFile profileImage);
}
