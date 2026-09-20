package com.sprint.mission.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.event.Level;

import java.net.HttpURLConnection;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum DiscodeitExceptionType implements ExceptionType {

    /**
     * HttpStatus 종류 + 뜻 (일단 자주 사용되는거만)
     *      (400) BAD_REQUEST = 값을 제대로 주세요
     *      (401) UNAUTHORIZED = 로그인 정보가 없거나 틀렸을 때
     *      (403) FORBIDDEN = 접근 권한 없음
     *      (404) NOT_FOUND = 데이터베이스에 존재하지 않음
     *      (409) CONFLICT = 데이터베이스에 존재하는데, 뭔가 충돌이 있음
     */

    // Auth
    LOGIN_REQUEST_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[AUTH] 로그인 요청 객체가 null임",
            "로그인 요청이 입력되지 않았습니다."
    ),

    LOGIN_USERNAME_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[AUTH] 로그인 요청의 username이 null임",
            "로그인할 username을 입력해주세요."
    ),

    LOGIN_PASSWORD_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[AUTH] 로그인 요청의 password가 null임",
            "로그인할 password를 입력해주세요."
    ),

    LOGIN_USER_NOT_FOUND(
            Level.WARN,
            HttpURLConnection.HTTP_NOT_FOUND,
            "[AUTH] 로그인할 User를 찾을 수 없음. username=%s",
            "요청한 User를 찾을 수 없습니다."
    ),

    LOGIN_WRONG_PASSWORD(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[AUTH] 비밀번호가 일치하지 않음",
            "비밀번호가 일치하지 않습니다."
    ),

    // User

    USER_ID_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[USER] User ID가 null임",
            "User ID를 입력해주세요."
    ),

    USER_USERNAME_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[USER] username이 null임",
            "username을 입력해주세요."
    ),

    USER_EMAIL_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[USER] email이 null임",
            "email을 입력해주세요."
    ),

    USER_PASSWORD_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[USER] password가 null임",
            "password를 입력해주세요."
    ),

    USER_NOT_FOUND(
            Level.WARN,
            HttpURLConnection.HTTP_NOT_FOUND,
            "[USER] User를 찾을 수 없음. userId=%s",
            "요청한 User를 찾을 수 없습니다."
    ),

    USER_USERNAME_EXISTS(
            Level.ERROR,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[USER] 동일한 username을 사용하는 User가 이미 존재함",
            "이미 사용 중인 username입니다."
    ),

    USER_EMAIL_EXISTS(
            Level.ERROR,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[USER] 동일한 email을 사용하는 User가 이미 존재함",
            "이미 사용 중인 email입니다."
    ),

    // Channel

    CHANNEL_ID_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[CHANNEL] Channel ID가 null임",
            "Channel ID를 입력해주세요."
    ),

    CHANNEL_NAME_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[CHANNEL] Channel name이 null임",
            "Channel 이름을 입력해주세요."
    ),

    CHANNEL_DESCRIPTION_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[CHANNEL] Channel description이 null임",
            "Channel 설명을 입력해주세요."
    ),

    PRIVATE_CHANNEL_PARTICIPANTS_IS_EMPTY(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[CHANNEL] PRIVATE Channel 참여자 목록이 비어 있음",
            "PRIVATE Channel 참여자를 한 명 이상 입력해주세요."
    ),

    CHANNEL_NOT_FOUND(
            Level.WARN,
            HttpURLConnection.HTTP_NOT_FOUND,
            "[CHANNEL] Channel을 찾을 수 없음. channelId=%s",
            "요청한 Channel을 찾을 수 없습니다."
    ),

    CHANNEL_ACCESS_DENIED(
            Level.ERROR,
            HttpURLConnection.HTTP_FORBIDDEN,
            "[CHANNEL] PRIVATE Channel 접근 거부. userId=%s, channelId=%s",
            "해당 Channel에 접근할 권한이 없습니다."
    ),

    PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED(
            Level.ERROR,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[CHANNEL] PRIVATE Channel 수정 시도. channelId=%s",
            "PRIVATE Channel은 수정할 수 없습니다."
    ),

    // Message

    MESSAGE_ID_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[MESSAGE] Message ID가 null임",
            "Message ID를 입력해주세요."
    ),

    MESSAGE_CONTENT_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[MESSAGE] Message content가 null임",
            "Message 내용을 입력해주세요."
    ),

    MESSAGE_CHANNEL_ID_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[MESSAGE] Message의 channelId가 null임",
            "Message가 속할 Channel ID를 입력해주세요."
    ),

    MESSAGE_SENDER_ID_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[MESSAGE] Message의 senderId가 null임",
            "Message 작성자의 User ID를 입력해주세요."
    ),

    MESSAGE_NOT_FOUND(
            Level.WARN,
            HttpURLConnection.HTTP_NOT_FOUND,
            "[MESSAGE] Message를 찾을 수 없음. messageId=%s",
            "요청한 Message를 찾을 수 없습니다."
    ),

    // ReadStatus

    READ_STATUS_ID_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[READ_STATUS] ReadStatus ID가 null임",
            "ReadStatus ID를 입력해주세요."
    ),

    READ_STATUS_NOT_FOUND(
            Level.WARN,
            HttpURLConnection.HTTP_NOT_FOUND,
            "[READ_STATUS] ReadStatus를 찾을 수 없음. readStatusId=%s",
            "요청한 ReadStatus를 찾을 수 없습니다."
    ),

    READ_STATUS_NOT_FOUND_BY_USER_AND_CHANNEL(
            Level.WARN,
            HttpURLConnection.HTTP_NOT_FOUND,
            "[READ_STATUS] ReadStatus를 찾을 수 없음. userId=%s, channelId=%s",
            "요청한 User와 Channel의 ReadStatus를 찾을 수 없습니다."
    ),

    READ_STATUS_ALREADY_EXISTS(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[READ_STATUS] ReadStatus가 이미 존재함. userId=%s, channelId=%s",
            "해당 User와 Channel의 ReadStatus가 이미 존재합니다."
    ),

    // UserStatus

    USER_STATUS_ID_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[USER_STATUS] UserStatus ID가 null임",
            "UserStatus ID를 입력해주세요."
    ),

    USER_STATUS_NOT_FOUND(
            Level.WARN,
            HttpURLConnection.HTTP_NOT_FOUND,
            "[USER_STATUS] UserStatus를 찾을 수 없음. id=%s",
            "요청한 UserStatus를 찾을 수 없습니다."
    ),

    USER_STATUS_ALREADY_EXISTS(
            Level.ERROR,
            HttpURLConnection.HTTP_CONFLICT,
            "[USER_STATUS] UserStatus가 이미 존재함. userId=%s",
            "해당 User의 UserStatus가 이미 존재합니다."
    ),

    // BinaryContent

    BINARY_CONTENT_ID_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[BIN_CONTENT] BinaryContent ID가 null임",
            "BinaryContent ID를 입력해주세요."
    ),

    BINARY_CONTENT_FILE_NAME_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[BINARY_CONTENT] BinaryContent fileName이 null임",
            "파일 이름을 입력해주세요."
    ),

    BINARY_CONTENT_BYTES_IS_NULL(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[BINARY_CONTENT] BinaryContent byte 데이터가 null임",
            "파일 데이터를 입력해주세요."
    ),

    BINARY_CONTENT_NOT_FOUND(
            Level.WARN,
            HttpURLConnection.HTTP_NOT_FOUND,
            "[BIN_CONTENT] BinaryContent를 찾을 수 없음. binaryContentId=%s",
            "요청한 BinaryContent를 찾을 수 없습니다."
    ),

    UNKNOWN_ERROR(
            Level.ERROR,
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            "[UNKNOWN] 예상하지 못한 오류가 발생함",
            "예상치 못한 오류가 발생했습니다."
    ),

    // File Repository

    FILE_LOAD_FAILED(
            Level.ERROR,
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            "[REPOSITORY] 파일 로드 실패. file=%s",
            "저장된 데이터를 불러오지 못했습니다."
    ),

    FILE_SAVE_FAILED(
            Level.ERROR,
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            "[REPOSITORY] 파일 저장 실패. file=%s",
            "데이터를 저장하지 못했습니다."
    ),

    FILE_IS_EMPTY(
            Level.ERROR,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[MULTIPART] 빈 파일. file=%s",
            "파일이 비어있습니다."
    );

    Level logLevel;
    int status;
    String description;
    String response;

    @Override
    public String formatLogMessage(Object... errorData) {
        if (Objects.isNull(errorData) || errorData.length == 0) {
            return description;
        }

        return description.formatted(errorData);    // String.format 비슷한 개념
    }
}
