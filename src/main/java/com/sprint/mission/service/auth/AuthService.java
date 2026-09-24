package com.sprint.mission.service.auth;

import com.sprint.mission.controller.dto.auth.LoginRequest;
import com.sprint.mission.controller.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.controller.dto.user.UserDto;
import com.sprint.mission.domain.User;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import com.sprint.mission.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

@Slf4j
@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public UserDto login(
            @NotNull @Valid LoginRequest loginRequest
    ) {
        User user = userRepository.getUserByUsername(loginRequest.getUsername());

        if (!user.matchesPassword(loginRequest.getPassword())) {
            log.error("로그인 실패. username={}", loginRequest.getUsername());
            throw new DiscodeitException(DiscodeitExceptionType.LOGIN_WRONG_PASSWORD);
        }

        log.info("로그인 완료: username={}", user.getUsername());

        return toUserDto(user);
    }

    private UserDto toUserDto(User user) {
        BinaryContentDto profile = Objects.isNull(user.getProfile())
                ? null
                : BinaryContentDto.from(user.getProfile());

        return UserDto.from(user, profile, user.getStatus());
    }
}
