package com.sprint.mission.repository.jcf;

import com.sprint.mission.domain.User;
import com.sprint.mission.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserRepository implements UserRepository {
    private static final Map<UUID, User> userMap = new HashMap<>();

    @Override
    public User save(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(userMap.get(userId));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMap.values().stream()
                .anyMatch(user ->
                        Objects.equals(user.getUsername(), username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMap.values().stream()
                .anyMatch(user ->
                        Objects.equals(user.getEmail(), email));
    }

    public Optional<User> findByUsername(String username) {
        return userMap.values().stream()
                .filter(user ->
                        Objects.equals(user.getUsername(), username))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return userMap.values().stream().toList();
    }

    @Override
    public void delete(UUID userId) {
        userMap.remove(userId);
    }
}
