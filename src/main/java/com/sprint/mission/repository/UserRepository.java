package com.sprint.mission.repository;

import com.sprint.mission.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // 목록 조회 시 profile / status 를 한 번에 가져와 N+1 방지
    @Override
    @EntityGraph(attributePaths = {"profile", "status"})
    List<User> findAll();
}
