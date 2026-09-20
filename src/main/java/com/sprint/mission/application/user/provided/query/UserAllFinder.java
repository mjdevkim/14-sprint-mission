package com.sprint.mission.application.user.provided.query;

import com.sprint.mission.application.user.dto.UserDto;

import java.util.List;

public interface UserAllFinder {
    List<UserDto> getAll();
}
