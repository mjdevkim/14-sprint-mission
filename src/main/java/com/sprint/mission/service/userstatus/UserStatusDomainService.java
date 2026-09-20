package com.sprint.mission.service.userstatus;

import com.sprint.mission.domain.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusDomainService {

    UserStatus create(UserStatus userStatus);

    UserStatus findById(UUID userStatusId);

    UserStatus findByUserId(UUID userId);

    List<UserStatus> findAll();

    UserStatus update(UserStatus updatingUserStatus);

    void delete(UUID userStatusId);
}
