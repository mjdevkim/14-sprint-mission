package com.sprint.mission.controller.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PageResponse<T> {
    private final List<T> content;    // 실제 데이터
    private final Object nextCursor;  // 다음 페이지 조회에 사용할 커서, 마지막 페이지면 null
    private final int size;           // 페이지 크기
    private final boolean hasNext;
    private final Long totalElements; // T 데이터의 총 개수, nullable
}
