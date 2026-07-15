package com.example.coderfolio.contexts.comment.infra;

import java.time.LocalDateTime;

// ============================================================
//  Comment  -  댓글 한 줄 (post_id 당 여러 개 가능)
//  Post/User 와 마찬가지로 JPA 표시 없는 순수 자바 객체.
// ============================================================
public class Comment {

    private Long id;
    private Long postId;       // 어느 글에 달린 댓글인지
    private String author;
    private String content;
    private LocalDateTime createdAt;

    // 새로 작성할 때 쓰는 생성자
    public Comment(Long postId, String author, String content) {
        this.postId = postId;
        this.author = author;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    // DB에서 읽어온 값을 채울 때 쓰는 생성자
    public Comment(Long id, Long postId, String author, String content, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getPostId() { return postId; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
