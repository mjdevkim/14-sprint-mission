package com.sprint.mission.controller.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PageResponse<T> {
    private final List<T> content;    // 실제 데이터
    private final int number; // 페이지 번호
    private final int size;   // 페이지 크기
    private final boolean hasNext;
    private final Long totalElements; // T 데이터의 총 개수, nullable
}
