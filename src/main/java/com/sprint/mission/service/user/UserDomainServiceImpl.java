package com.sprint.mission.service.user;

import com.sprint.mission.domain.User;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import com.sprint.mission.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Qualifier("userService")
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;

    public UserDomainServiceImpl(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public void validateUnique(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new DiscodeitException(DiscodeitExceptionType.USER_USERNAME_EXISTS);
        }
        if (userRepository.existsByEmail(email)) {
            throw new DiscodeitException(DiscodeitExceptionType.USER_EMAIL_EXISTS);
        }
    }

    @Override
    public User create(User user) {
        validateUnique(user.getUsername(), user.getEmail());

        return userRepository.save(user);
    }

    @Override
    public User findById(UUID userId) {
        if (Objects.isNull(userId)) {
            throw new DiscodeitException(DiscodeitExceptionType.USER_ID_IS_NULL);
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new DiscodeitException(DiscodeitExceptionType.USER_NOT_FOUND, userId));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User updatingUser) { // Replace 로직
        User originalUser = findById(updatingUser.getId());
        validateUnique(
                originalUser,
                updatingUser.getUsername(),
                updatingUser.getEmail()
        );
        return userRepository.save(updatingUser);
    }

    private void validateUnique(
            User originalUser,
            String username,
            String email
    ) {
        boolean usernameChanged = !Objects.equals(
                originalUser.getUsername(),
                username
        );
        boolean emailChanged = !Objects.equals(
                originalUser.getEmail(),
                email
        );

        if (usernameChanged && userRepository.existsByUsername(username)) {
            throw new DiscodeitException(DiscodeitExceptionType.USER_USERNAME_EXISTS);
        }
        if (emailChanged && userRepository.existsByEmail(email)) {
            throw new DiscodeitException(DiscodeitExceptionType.USER_EMAIL_EXISTS);
        }
    }

    @Override
    public void delete(UUID userId) {
        findById(userId);
        userRepository.delete(userId);
    }
}
