package com.example.coderfolio.contexts.comment.application;

import com.example.coderfolio.contexts.comment.application.dto.AddCommentCommand;
import com.example.coderfolio.contexts.comment.application.dto.CommentResult;
import com.example.coderfolio.contexts.comment.infra.Comment;
import com.example.coderfolio.contexts.comment.infra.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// ============================================================
//  CommentService  -  댓글 규칙 담당 (PostService와 완전히 같은 패턴)
//  api 계층의 DTO는 전혀 모름. application 전용 Command/Result로만 대화함.
// ============================================================
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // 특정 글에 달린 댓글 전체 (오래된 순)
    public List<CommentResult> getComments(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(CommentResult::from)
                .toList();
    }

    // 댓글 작성 규칙: 빈 내용 금지
    public void addComment(AddCommentCommand command) {
        if (command.getContent() == null || command.getContent().isBlank()) {
            throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
        }
        commentRepository.save(new Comment(command.getPostId(), command.getAuthor(), command.getContent()));
    }

    // 댓글 삭제 규칙: 댓글이 있어야 함 + "본인 댓글"만 가능
    public void deleteComment(Long id, String requester) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없어요."));
        if (!comment.getAuthor().equals(requester)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 쓴 댓글만 삭제할 수 있어요.");
        }
        commentRepository.deleteById(id, requester);
    }
}
