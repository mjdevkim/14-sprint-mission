package com.sprint.mission.application.user;

import com.sprint.mission.application.user.provided.command.UserCommand;
import com.sprint.mission.application.user.provided.query.UserEntityFinder;
import com.sprint.mission.common.exception.DiscodeitException;
import com.sprint.mission.common.exception.DiscodeitExceptionType;
import com.sprint.mission.domain.User;
import com.sprint.mission.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserQueryService implements UserCommand {

    private final UserRepository userRepository;    // required
    private final UserEntityFinder userEntityFinder;    // provided

    @Override
    public User create(User user) {
        validateUnique(user.getUsername(), user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User update(User updatingUser) {
        User originalUser = userEntityFinder.getUser(updatingUser.getId());
        validateUniqueOnUpdate(
                originalUser,
                updatingUser.getUsername(),
                updatingUser.getEmail()
        );
        return userRepository.save(updatingUser);
    }

    @Override
    public void delete(UUID userId) {
        userEntityFinder.getUser(userId); // 존재 여부 확인 (없으면 USER_NOT_FOUND)
        userRepository.delete(userId);
    }

    // ============================================================================

    // username, email 중복 검사
    private void validateUnique(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new DiscodeitException(DiscodeitExceptionType.USER_USERNAME_EXISTS);
        }
        if (userRepository.existsByEmail(email)) {
            throw new DiscodeitException(DiscodeitExceptionType.USER_EMAIL_EXISTS);
        }
    }

    // update할때 값이 바뀌면 username, email 중복 검사
    private void validateUniqueOnUpdate(
            User originalUser,
            String username,
            String email
    ) {
        boolean usernameChanged = !Objects.equals(originalUser.getUsername(), username);
        boolean emailChanged = !Objects.equals(originalUser.getEmail(), email);

        if (usernameChanged && userRepository.existsByUsername(username)) {
            throw new DiscodeitException(DiscodeitExceptionType.USER_USERNAME_EXISTS);
        }
        if (emailChanged && userRepository.existsByEmail(email)) {
            throw new DiscodeitException(DiscodeitExceptionType.USER_EMAIL_EXISTS);
        }
    }
}
