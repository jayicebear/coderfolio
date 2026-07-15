package com.example.coderfolio.contexts.comment.api.dto;

import com.example.coderfolio.contexts.comment.application.dto.CommentResult;
import java.time.LocalDateTime;

// ============================================================
//  CommentResponse  -  화면에 넘길 값만 담는 DTO (api 계층 전용, PostViewResponse와 같은 패턴)
// ============================================================
public class CommentResponse {

    private final Long id;
    private final Long postId;
    private final String author;
    private final String content;
    private final LocalDateTime createdAt;

    public CommentResponse(Long id, Long postId, String author, String content, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static CommentResponse from(CommentResult result) {
        return new CommentResponse(
                result.getId(), result.getPostId(), result.getAuthor(), result.getContent(), result.getCreatedAt()
        );
    }

    public Long getId() { return id; }
    public Long getPostId() { return postId; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
