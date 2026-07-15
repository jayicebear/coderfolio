package com.example.coderfolio.contexts.board.application.dto;

// ============================================================
//  WritePostCommand  -  글쓰기 "업무 처리"에 필요한 값만 담는 DTO
//  api의 PostWriteApiRequest에는 없던 author(작성자)까지 포함함
//  (작성자는 세션에서 얻는 정보라, api dto가 아니라 Controller가 조합해서 넘김)
// ============================================================
public class WritePostCommand {

    private final String title;
    private final String content;
    private final String author;

    public WritePostCommand(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
}