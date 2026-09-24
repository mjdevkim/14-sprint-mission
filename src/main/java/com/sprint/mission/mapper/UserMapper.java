package com.sprint.mission.mapper;

import com.sprint.mission.controller.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.controller.dto.user.UserDto;
import com.sprint.mission.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final BinaryContentMapper binaryContentMapper;

    public UserDto toDto(User user) {
        if (Objects.isNull(user)) {
            return null;
        }

        BinaryContentDto profile = binaryContentMapper.toDto(user.getProfile());
        return UserDto.from(user, profile, user.getStatus());
    }
}
