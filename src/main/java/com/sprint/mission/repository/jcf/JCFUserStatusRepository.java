package com.sprint.mission.repository.jcf;

import com.sprint.mission.domain.UserStatus;
import com.sprint.mission.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserStatusRepository implements UserStatusRepository {

    private static final Map<UUID, UserStatus> userStatusMap = new HashMap<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        userStatusMap.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        return Optional.ofNullable(userStatusMap.get(userStatusId));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return userStatusMap.values().stream()
                .filter(userStatus ->
                        Objects.equals(userStatus.getUserId(), userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusMap.values().stream().toList();
    }

    @Override
    public void delete(UUID userStatusId) {
        userStatusMap.remove(userStatusId);
    }
}
