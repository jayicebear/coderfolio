package com.example.coderfolio.contexts.board.application.dto;

import com.example.coderfolio.contexts.board.infra.Post;
import java.time.LocalDateTime;

// ============================================================
//  PostResult  -  application 계층이 돌려주는 결과 (api 계층 전용 모양은 아님)
//  Post 엔티티를 밖으로 안 내보내고, 이 DTO로 감싸서 돌려줌.
// ============================================================
public class PostResult {

    private final Long id;
    private final String title;
    private final String content;
    private final String author;
    private final LocalDateTime createdAt;

    public PostResult(Long id, String title, String content, String author, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    // infra의 Post 엔티티 -> application의 PostResult로 변환
    public static PostResult from(Post post) {
        return new PostResult(
            post.getId(), post.getTitle(), post.getContent(), post.getAuthor(), post.getCreatedAt()
        );
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}