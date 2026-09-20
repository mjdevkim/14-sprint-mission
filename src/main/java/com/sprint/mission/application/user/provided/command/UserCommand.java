package com.sprint.mission.application.user.provided.command;

import com.sprint.mission.application.user.dto.UserCreateRequest;
import com.sprint.mission.application.user.dto.UserResponseDto;
import com.sprint.mission.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserCommand {
    User create(User user);
    User update(User user);
    void delete(UUID userId);
}
