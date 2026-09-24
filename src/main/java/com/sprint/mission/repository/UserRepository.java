package com.sprint.mission.repository;

import com.sprint.mission.domain.User;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Override
    @EntityGraph(attributePaths = {"profile", "status"})
    List<User> findAll();

    default User getUser(UUID userId) {
        if (Objects.isNull(userId)) {
            throw new DiscodeitException(DiscodeitExceptionType.USER_ID_IS_NULL);
        }

        return findById(userId)
                .orElseThrow(() -> new DiscodeitException(DiscodeitExceptionType.USER_NOT_FOUND, userId));
    }

    default User getUserByUsername(String username) {
        return findByUsername(username)    // username은 고유하다
                .orElseThrow(() -> new DiscodeitException(DiscodeitExceptionType.LOGIN_USER_NOT_FOUND, username));
    }
}
