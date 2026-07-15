package com.example.coderfolio.contexts.board.infra;

import java.time.LocalDateTime;

// ============================================================
//  Post  -  순수 자바 객체 (POJO). JPA 표시 없음.
//  작성 시각은 "새 글을 만드는 시점"에 이 생성자 안에서 직접 채워요
//  (예전 @PrePersist가 하던 일을 이제 우리가 코드로 직접 함).
// ============================================================
public class Post {

    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdAt;

    // 새 글 작성할 때 쓰는 생성자
    public Post(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();   // 저장하기 직전, 지금 이 순간을 작성 시각으로
    }

    // DB에서 읽어온 값을 채울 때 쓰는 생성자
    public Post(Long id, String title, String content, String author, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}