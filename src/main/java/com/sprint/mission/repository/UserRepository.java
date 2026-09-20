package com.sprint.mission.repository;

import com.sprint.mission.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// 저장로직만
public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID userId);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findAll();
    void delete(UUID userId);
}
