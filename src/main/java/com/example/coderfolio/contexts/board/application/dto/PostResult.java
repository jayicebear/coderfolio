package com.example.coderfolio.contexts.board.application.dto;

import com.example.coderfolio.contexts.board.infra.Post;
import java.time.LocalDateTime;

// ============================================================
//  PostResult  -  application 계층이 돌려주는 결과 (api 계층 전용 모양은 아님)
//  Post 엔티티를 밖으로 안 내보내고, 이 DTO로 감싸서 돌려줌.
//  likedByMe: "지금 보고 있는 사람"이 좋아요를 눌렀는지 — Post(DB 값)가 아니라
//  보는 사람에 따라 달라지는 값이라, 상세 조회 때만 Service가 채워서 넘김.
// ============================================================
public class PostResult {

    private final Long id;
    private final String title;
    private final String content;
    private final String author;
    private final long viewCount;
    private final long likeCount;
    private final boolean likedByMe;
    private final LocalDateTime createdAt;

    public PostResult(Long id, String title, String content, String author,
                      long viewCount, long likeCount, boolean likedByMe, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.likedByMe = likedByMe;
        this.createdAt = createdAt;
    }

    // 목록용: 보는 사람이 누군지 따지지 않으므로 likedByMe는 false로 둠
    public static PostResult from(Post post) {
        return from(post, false);
    }

    // 상세용: 로그인한 사람의 좋아요 여부까지 채워서 변환
    public static PostResult from(Post post, boolean likedByMe) {
        return new PostResult(
            post.getId(), post.getTitle(), post.getContent(), post.getAuthor(),
            post.getViewCount(), post.getLikeCount(), likedByMe, post.getCreatedAt()
        );
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public long getViewCount() { return viewCount; }
    public long getLikeCount() { return likeCount; }
    public boolean isLikedByMe() { return likedByMe; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
