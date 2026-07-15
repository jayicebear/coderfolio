package com.example.coderfolio.contexts.comment.api;

import com.example.coderfolio.contexts.comment.api.dto.CommentForm;
import com.example.coderfolio.contexts.comment.api.dto.CommentResponse;
import com.example.coderfolio.contexts.comment.application.CommentService;
import com.example.coderfolio.contexts.comment.application.dto.AddCommentCommand;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// ============================================================
//  CommentApiController  -  댓글 JSON API
//  - 조회/작성은 글 하나에 종속되므로 /api/posts/{postId}/comments 경로를 씀
//  - 삭제는 댓글 id 하나로 충분하므로 /api/comments/{id} 경로를 씀
//  - 목록 조회는 누구나, 작성/삭제는 로그인 필요 (본인 댓글만 삭제 가능)
// ============================================================
@RestController
public class CommentApiController {

    private final CommentService commentService;

    @Autowired
    public CommentApiController(CommentService commentService) {
        this.commentService = commentService;
    }

    // GET /api/posts/{postId}/comments  -  특정 글의 댓글 목록 (누구나)
    @GetMapping("/api/posts/{postId}/comments")
    public List<CommentResponse> list(@PathVariable Long postId) {
        return commentService.getComments(postId).stream().map(CommentResponse::from).toList();
    }

    // POST /api/posts/{postId}/comments  -  댓글 작성 (로그인 필요)
    @PostMapping("/api/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void write(@PathVariable Long postId, @RequestBody CommentForm form, HttpSession session) {
        String loginUser = requireLogin(session);
        commentService.addComment(new AddCommentCommand(postId, loginUser, form.getContent()));
    }

    // DELETE /api/comments/{id}  -  댓글 삭제 (로그인 + 본인 댓글만)
    @DeleteMapping("/api/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, HttpSession session) {
        String loginUser = requireLogin(session);
        commentService.deleteComment(id, loginUser);
    }

    private String requireLogin(HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요해요.");
        }
        return loginUser;
    }
}
