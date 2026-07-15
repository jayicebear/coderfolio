package com.example.coderfolio.contexts.comment.application;

import com.example.coderfolio.contexts.comment.application.dto.AddCommentCommand;
import com.example.coderfolio.contexts.comment.application.dto.CommentResult;
import com.example.coderfolio.contexts.comment.infra.Comment;
import com.example.coderfolio.contexts.comment.infra.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// ============================================================
//  CommentServiceTest  -  댓글 규칙 검증 (PostServiceTest와 같은 패턴)
//  핵심: "남의 댓글은 삭제 못 한다" (403)
// ============================================================
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentService commentService;

    private Comment existingComment() {
        return new Comment(1L, 10L, "maru", "댓글 내용", LocalDateTime.of(2026, 7, 15, 12, 0));
    }

    // ---------------- 조회 ----------------

    @Test
    @DisplayName("특정 글의 댓글 목록을 오래된 순으로 조회")
    void getComments_returnsList() {
        when(commentRepository.findByPostId(10L)).thenReturn(List.of(existingComment()));

        List<CommentResult> results = commentService.getComments(10L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getContent()).isEqualTo("댓글 내용");
        assertThat(results.get(0).getPostId()).isEqualTo(10L);
    }

    // ---------------- 작성 ----------------

    @Test
    @DisplayName("내용이 비어있으면 댓글 작성 실패")
    void addComment_blankContent_fails() {
        assertThatThrownBy(() -> commentService.addComment(new AddCommentCommand(10L, "maru", "")))
                .isInstanceOf(IllegalArgumentException.class);
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("정상 작성이면 저장됨")
    void addComment_success_saves() {
        commentService.addComment(new AddCommentCommand(10L, "maru", "댓글 내용"));

        verify(commentRepository).save(any(Comment.class));
    }

    // ---------------- 삭제 ----------------

    @Test
    @DisplayName("없는 댓글을 삭제하려 하면 404")
    void deleteComment_missing_throws404() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(99L, "maru"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("본인 댓글이면 삭제 성공")
    void deleteComment_owner_succeeds() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(existingComment()));

        commentService.deleteComment(1L, "maru");

        verify(commentRepository).deleteById(1L, "maru");
    }

    @Test
    @DisplayName("남의 댓글을 삭제하려 하면 403")
    void deleteComment_notOwner_throws403() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(existingComment()));   // 작성자는 maru

        assertThatThrownBy(() -> commentService.deleteComment(1L, "hacker"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.FORBIDDEN);
        verify(commentRepository, never()).deleteById(any(), anyString());
    }
}
