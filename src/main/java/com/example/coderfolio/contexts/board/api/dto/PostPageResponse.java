package com.example.coderfolio.contexts.board.api.dto;

import com.example.coderfolio.contexts.board.application.dto.PostPageResult;

import java.util.List;

// ============================================================
//  PostPageResponse  -  게시글 목록 API가 실제로 내려주는 JSON 모양 (api 계층 전용)
//  application의 PostPageResult를 받아서 화면 전용 DTO(PostViewResponse) 목록으로 감쌈.
// ============================================================
public class PostPageResponse {

    private final List<PostViewResponse> posts;
    private final int page;
    private final int size;
    private final long totalCount;
    private final int totalPages;

    public PostPageResponse(List<PostViewResponse> posts, int page, int size, long totalCount, int totalPages) {
        this.posts = posts;
        this.page = page;
        this.size = size;
        this.totalCount = totalCount;
        this.totalPages = totalPages;
    }

    public static PostPageResponse from(PostPageResult result) {
        return new PostPageResponse(
                result.getPosts().stream().map(PostViewResponse::from).toList(),
                result.getPage(),
                result.getSize(),
                result.getTotalCount(),
                result.getTotalPages()
        );
    }

    public List<PostViewResponse> getPosts() { return posts; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalCount() { return totalCount; }
    public int getTotalPages() { return totalPages; }
}
