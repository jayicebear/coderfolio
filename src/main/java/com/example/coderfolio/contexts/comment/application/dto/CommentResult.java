package com.example.coderfolio.contexts.comment.application.dto;

import com.example.coderfolio.contexts.comment.infra.Comment;
import java.time.LocalDateTime;

// ============================================================
//  CommentResult  -  application 계층이 돌려주는 결과 (PostResult와 같은 패턴)
// ============================================================
public class CommentResult {

    private final Long id;
    private final Long postId;
    private final String author;
    private final String content;
    private final LocalDateTime createdAt;

    public CommentResult(Long id, Long postId, String author, String content, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static CommentResult from(Comment comment) {
        return new CommentResult(
                comment.getId(), comment.getPostId(), comment.getAuthor(), comment.getContent(), comment.getCreatedAt()
        );
    }

    public Long getId() { return id; }
    public Long getPostId() { return postId; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
