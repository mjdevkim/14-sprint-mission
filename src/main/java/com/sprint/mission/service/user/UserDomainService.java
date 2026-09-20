package com.sprint.mission.service.user;

import com.sprint.mission.domain.User;

import java.util.List;
import java.util.UUID;

public interface UserDomainService {
    void validateUnique(String username, String email);

    User create(User user);

    User findById(UUID userId);

    List<User> findAll();

    User update(User updatingUser);

    void delete(UUID userId);
}
