package com.example.coderfolio.contexts.board.application.dto;

import java.util.List;

// ============================================================
//  PostPageResult  -  "한 페이지 분량의 글 목록 + 페이지 정보"를 담는 결과 DTO
//  목록(posts) 자체와, 화면이 페이지 버튼을 그리는 데 필요한 정보를 함께 묶어서 돌려줌.
// ============================================================
public class PostPageResult {

    private final List<PostResult> posts;
    private final int page;         // 지금 몇 페이지인지 (1부터 시작)
    private final int size;         // 한 페이지에 몇 개씩
    private final long totalCount;  // (검색 조건 반영한) 전체 글 개수
    private final int totalPages;   // 전체 페이지 수

    public PostPageResult(List<PostResult> posts, int page, int size, long totalCount, int totalPages) {
        this.posts = posts;
        this.page = page;
        this.size = size;
        this.totalCount = totalCount;
        this.totalPages = totalPages;
    }

    public List<PostResult> getPosts() { return posts; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalCount() { return totalCount; }
    public int getTotalPages() { return totalPages; }
}
