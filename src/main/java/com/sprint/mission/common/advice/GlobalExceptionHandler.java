package com.sprint.mission.advice;

import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.ExceptionType;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<String> handle(DiscodeitException exception) {
        ExceptionType discodeitExceptionType = exception.getType();
        log.makeLoggingEventBuilder(discodeitExceptionType.getLogLevel())
                .setCause(exception)
                .log(exception.getMessage());
        return ResponseEntity
                .status(discodeitExceptionType.getStatus())
                .body(discodeitExceptionType.getResponse());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HandlerMethodValidationException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<String> handleBadRequest(Exception exception) {
        log.warn("잘못된 API 요청", exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("잘못된 요청입니다.");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public void handle(Exception exception) {
        log.error("우리가 커버하지 못한 예외 발생", exception);
    }
}
