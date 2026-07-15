package com.example.coderfolio.contexts.comment.api.dto;

// ============================================================
//  CommentForm  -  댓글 작성 폼에서 오는 입력 (postId는 URL 경로에서, author는 세션에서 옴)
// ============================================================
public class CommentForm {

    private String content;

    public CommentForm() {}

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
