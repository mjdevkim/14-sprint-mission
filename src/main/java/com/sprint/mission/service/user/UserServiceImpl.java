package com.sprint.mission.service.user;

import com.sprint.mission.controller.dto.user.UserCreateRequest;
import com.sprint.mission.controller.dto.user.UserDto;
import com.sprint.mission.controller.dto.user.UserUpdateRequest;
import com.sprint.mission.controller.dto.userstatus.UserStatusDto;
import com.sprint.mission.controller.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.storage.BinaryContentStorage;
import com.sprint.mission.domain.BinaryContent;
import com.sprint.mission.domain.User;
import com.sprint.mission.domain.UserStatus;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import com.sprint.mission.mapper.UserMapper;
import com.sprint.mission.mapper.UserStatusMapper;
import com.sprint.mission.multipart.MultipartFileConverter;
import com.sprint.mission.multipart.MultipartFileDto;
import com.sprint.mission.repository.BinaryContentRepository;
import com.sprint.mission.repository.UserRepository;
import com.sprint.mission.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserStatusRepository userStatusRepository;
    private final MultipartFileConverter multipartFileConverter;
    private final UserMapper userMapper;
    private final UserStatusMapper userStatusMapper;

    @Override
    public UserDto create(
            UserCreateRequest userCreateRequest,
            MultipartFile profileImageRequest
    ) {
        // generate binary content
        BinaryContent createdProfileImage = null;
        if (Objects.nonNull(profileImageRequest)) {
            createdProfileImage = createBinaryContent(profileImageRequest);
            log.info(
                    "새 프로필 저장 완료: newProfileId={}",
                    createdProfileImage.getId()
            );
        }

        log.info(
                "User 생성 시작: username={}, email={}, profileImage={}",
                userCreateRequest.getUsername(),
                userCreateRequest.getEmail(),
                Objects.isNull(createdProfileImage) ? "No Pfp" : createdProfileImage.getId()
        );

        validateUnique(userCreateRequest.getUsername(), userCreateRequest.getEmail());

        User createdUser = userRepository.save(User.create(
                userCreateRequest.getUsername(),
                userCreateRequest.getEmail(),
                userCreateRequest.getPassword(),
                createdProfileImage
        ));

        log.info(
                "User 생성 완료: userId={}, profileId={}",
                createdUser.getId(),
                Objects.isNull(createdUser.getProfile()) ? null : createdUser.getProfile().getId()
        );

        return userMapper.toDto(createdUser);
    }

    private BinaryContent createBinaryContent(MultipartFile profileImageRequest) {
        MultipartFileDto converted = multipartFileConverter.convert(profileImageRequest);
        BinaryContent binaryContent = BinaryContent.create( // 메타데이터만 저장
                converted.getFileName(),
                converted.getContentType(),
                converted.getBytes().length
        );
        // binary content repository 저장
        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

        // binary content storage에 byte[] 저장
        binaryContentStorage.put(savedBinaryContent.getId(), converted.getBytes());

        return savedBinaryContent;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(UUID userId) {
        log.debug("User 단일 조회: userId={}", userId);
        User user = userRepository.getUser(userId);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<UserDto> userResponses = userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();

        log.debug("User 다건 조회: size={}", userResponses.size());
        return userResponses;
    }

    @Override
    public UserDto update(
            UUID userId,
            UserUpdateRequest userUpdateRequest,
            MultipartFile profileImage
    ) {
        User updatingUser = userRepository.getUser(userId);
        BinaryContent oldProfile = updatingUser.getProfile();

        log.info(
                "User 수정 시작: userId={}, replaceProfile={}",
                userId,
                Objects.nonNull(profileImage) ? "YES" : "N/A"
        );

        // 프로필 사진 있으면 생성
        BinaryContent createdProfileImage = null;
        if (Objects.nonNull(profileImage)) {
            createdProfileImage = createBinaryContent(profileImage);
            log.info(
                    "새 프로필 저장 완료: userId={}, newProfileId={}",
                    userId,
                    createdProfileImage.getId()
            );
        }

        validateUniqueForUpdate(
                updatingUser,
                userUpdateRequest.getNewUsername(),
                userUpdateRequest.getNewEmail()
        );

        // user 정보 수정 - 엔티티에서 수행한다
        updatingUser.updateAccountDetails(
                userUpdateRequest.getNewUsername(),
                userUpdateRequest.getNewEmail(),
                userUpdateRequest.getNewPassword(),
                createdProfileImage
        );
        User updatedUser = updatingUser;

        if (Objects.nonNull(createdProfileImage) && Objects.nonNull(oldProfile)) {
            binaryContentRepository.deleteById(oldProfile.getId());
            log.debug(
                    "기존 프로필 삭제 완료: userId={}, oldProfileId={}",
                    userId,
                    oldProfile.getId()
            );
        }

        log.info(
                "User 수정 완료: userId={}, profileId={}",
                updatedUser.getId(),
                Objects.isNull(updatedUser.getProfile()) ? null : updatedUser.getProfile().getId()
        );

        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserStatusDto updateUserStatusByUserId(
            UUID userId,
            UserStatusUpdateRequest request
    ) {
        log.info("User 업데이트 시작: userId={}", userId);
        userRepository.getUser(userId);
        UserStatus updatingUserStatus = userStatusRepository.getUserStatusByUserId(userId);

        updatingUserStatus.updateLastActiveAt(request.getNewLastActiveAt());
        UserStatus updatedUserStatus = updatingUserStatus;

        log.info(
                "UserStatus 업데이트 완료: userId={}, lastActiveAt={}",
                userId,
                updatedUserStatus.getLastActiveAt()
        );

        return userStatusMapper.toDto(updatedUserStatus);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.getUser(userId);
        BinaryContent profile = user.getProfile();
        UUID userStatusId = user.getStatus().getId();

        log.info("User 삭제 시작: userId={}", userId);

        userRepository.deleteById(userId);
        if (Objects.nonNull(profile)) {
            binaryContentRepository.deleteById(profile.getId());
        }

        log.info(
                "User 삭제 완료: userId={}, userStatusId={}, profileId={}",
                userId,
                userStatusId,
                Objects.isNull(profile) ? null : profile.getId()
        );
    }

    private void validateUnique(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new DiscodeitException(DiscodeitExceptionType.USER_USERNAME_EXISTS);
        }
        if (userRepository.existsByEmail(email)) {
            throw new DiscodeitException(DiscodeitExceptionType.USER_EMAIL_EXISTS);
        }
    }

    private void validateUniqueForUpdate(User currentUser, String newUsername, String newEmail) {
        boolean usernameChanged = Objects.nonNull(newUsername)
                && !Objects.equals(currentUser.getUsername(), newUsername);
        boolean emailChanged = Objects.nonNull(newEmail)
                && !Objects.equals(currentUser.getEmail(), newEmail);

        if (usernameChanged && userRepository.existsByUsername(newUsername)) {
            throw new DiscodeitException(DiscodeitExceptionType.USER_USERNAME_EXISTS);
        }
        if (emailChanged && userRepository.existsByEmail(newEmail)) {
            throw new DiscodeitException(DiscodeitExceptionType.USER_EMAIL_EXISTS);
        }
    }
}
