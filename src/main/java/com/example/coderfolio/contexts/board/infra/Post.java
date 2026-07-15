package com.example.coderfolio.contexts.board.infra;

import java.time.LocalDateTime;

// ============================================================
//  Post  -  순수 자바 객체 (POJO). JPA 표시 없음.
//  작성 시각은 "새 글을 만드는 시점"에 이 생성자 안에서 직접 채워요
//  (예전 @PrePersist가 하던 일을 이제 우리가 코드로 직접 함).
//  viewCount는 posts 테이블의 컬럼이고, likeCount는 post_likes 줄 수를
//  조회 시점에 COUNT로 세서 함께 담아오는 "계산된 값"이에요.
// ============================================================
public class Post {

    private Long id;
    private String title;
    private String content;
    private String author;
    private long viewCount;
    private long likeCount;
    private LocalDateTime createdAt;

    // 새 글 작성할 때 쓰는 생성자 (조회수/좋아요는 0에서 시작)
    public Post(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.viewCount = 0;
        this.likeCount = 0;
        this.createdAt = LocalDateTime.now();   // 저장하기 직전, 지금 이 순간을 작성 시각으로
    }

    // DB에서 읽어온 값을 채울 때 쓰는 생성자
    public Post(Long id, String title, String content, String author,
                long viewCount, long likeCount, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public long getViewCount() { return viewCount; }
    public long getLikeCount() { return likeCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
