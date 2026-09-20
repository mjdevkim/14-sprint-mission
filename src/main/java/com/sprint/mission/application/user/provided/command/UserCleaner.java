package com.sprint.mission.application.user.provided.command;

import java.util.UUID;

public interface UserCleaner {
    void deleteById(UUID userId);
}
