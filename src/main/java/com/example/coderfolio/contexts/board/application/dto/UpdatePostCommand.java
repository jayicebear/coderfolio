package com.example.coderfolio.contexts.board.application.dto;

// ============================================================
//  UpdatePostCommand  -  글 수정에 필요한 값
//  WritePostCommand와 달리 "어느 글인지" 가리키는 id가 추가로 필요함.
//  author는 "수정을 요청한 사람"(세션의 로그인 사용자) — 본인 글인지 검사에 쓰임.
// ============================================================
public class UpdatePostCommand {

    private final Long id;
    private final String title;
    private final String content;
    private final String author;

    public UpdatePostCommand(Long id, String title, String content, String author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
}
