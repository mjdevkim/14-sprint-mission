package com.sprint.mission.application.user.provided.command;

import com.sprint.mission.application.user.dto.UserCreateRequest;
import com.sprint.mission.application.user.dto.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserRegister {
    UserResponseDto register(UserCreateRequest request, MultipartFile profileImage);
}
