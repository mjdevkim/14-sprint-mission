package com.sprint.mission.exception;

import lombok.Getter;

@Getter
public class DiscodeitException extends RuntimeException {

    private final ExceptionType type;

    public DiscodeitException(
            ExceptionType type,
            Object... errorData
    ) {
        super(type.formatLogMessage(errorData));
        this.type = type;
    }

    public DiscodeitException(
            ExceptionType type,
            Throwable cause,
            Object... errorData
    ) {
        super(type.formatLogMessage(errorData), cause);
        this.type = type;
    }
}
