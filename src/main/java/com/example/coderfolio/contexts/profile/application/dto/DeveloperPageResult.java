package com.example.coderfolio.contexts.profile.application.dto;

import com.example.coderfolio.contexts.profile.infra.DeveloperSummary;

import java.util.List;

// ============================================================
//  DeveloperPageResult  -  "개발자 둘러보기" 한 페이지 분량 + 페이지 정보
//  board의 PostPageResult와 같은 패턴.
//  목록 항목(DeveloperSummary)은 값만 담은 읽기 전용 객체라 그대로 담음
//  (ProfileResult가 Education 등을 그대로 담는 것과 같은, 이 도메인의 단순화 방침).
// ============================================================
public class DeveloperPageResult {

    private final List<DeveloperSummary> developers;
    private final int page;
    private final int size;
    private final long totalCount;
    private final int totalPages;

    public DeveloperPageResult(List<DeveloperSummary> developers, int page, int size, long totalCount, int totalPages) {
        this.developers = developers;
        this.page = page;
        this.size = size;
        this.totalCount = totalCount;
        this.totalPages = totalPages;
    }

    public List<DeveloperSummary> getDevelopers() { return developers; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalCount() { return totalCount; }
    public int getTotalPages() { return totalPages; }
}
