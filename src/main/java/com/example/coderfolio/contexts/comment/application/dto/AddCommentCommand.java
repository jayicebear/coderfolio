package com.example.coderfolio.contexts.comment.application.dto;

// ============================================================
//  AddCommentCommand  -  댓글 작성에 필요한 값
//  author는 화면 폼이 아니라 세션(로그인 정보)에서 오므로 Controller가 채워 넘김.
// ============================================================
public class AddCommentCommand {

    private final Long postId;
    private final String author;
    private final String content;

    public AddCommentCommand(Long postId, String author, String content) {
        this.postId = postId;
        this.author = author;
        this.content = content;
    }

    public Long getPostId() { return postId; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
}
