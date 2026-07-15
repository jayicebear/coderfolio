package com.example.coderfolio.contexts.board.api.dto;

import com.example.coderfolio.contexts.board.application.dto.PostResult;
import java.time.LocalDateTime;

// ============================================================
//  PostViewResponse  -  화면(Thymeleaf 템플릿)에 넘길 값만 담는 DTO (api 계층 전용)
//  application 계층의 PostResult를 받아서 api 계층 전용 모양으로 한 번 더 감싸요.
// ============================================================
public class PostViewResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String author;
    private final LocalDateTime createdAt;

    public PostViewResponse(Long id, String title, String content, String author, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    // application의 PostResult -> api의 PostViewResponse로 변환
    public static PostViewResponse from(PostResult result) {
        return new PostViewResponse(
            result.getId(), result.getTitle(), result.getContent(), result.getAuthor(), result.getCreatedAt()
        );
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}