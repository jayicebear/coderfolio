package com.example.coderfolio.contexts.board.api.dto;

// ============================================================
//  PostWriteApiRequest  -  글쓰기 "화면 폼"에서 오는 입력을 담는 DTO
// ============================================================
public class PostWriteApiRequest {

    private String title;
    private String content;

    public PostWriteApiRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}