package com.sprint.mission.multipart;

import com.sprint.mission.exception.ExceptionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.event.Level;

import java.net.HttpURLConnection;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum MultipartFileExceptionType implements ExceptionType {

    FILE_IS_EMPTY(
            Level.WARN,
            HttpURLConnection.HTTP_BAD_REQUEST,
            "[MULTIPART] 파일 객체가 비어있음",
            "요청한 파일 객체 변환에 실패했습니다."),

    MULTIPART_FILE_READ_FAILED(
            Level.ERROR,
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            "[MULTIPART] 파일 객체 읽기 실패",
            "파일 객체를 읽기를 실패했습니다."
    );

    Level logLevel;
    int status;
    String description;
    String response;

    @Override
    public String formatLogMessage(Object... errorData) {
        return description;
    }
}
