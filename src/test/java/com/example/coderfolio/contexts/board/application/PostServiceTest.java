package com.example.coderfolio.contexts.board.application;

import com.example.coderfolio.contexts.board.application.dto.PostResult;
import com.example.coderfolio.contexts.board.application.dto.UpdatePostCommand;
import com.example.coderfolio.contexts.board.application.dto.WritePostCommand;
import com.example.coderfolio.contexts.board.infra.Post;
import com.example.coderfolio.contexts.board.infra.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// ============================================================
//  PostServiceTest  -  게시판 규칙 검증
//  특히 중요한 것: "남의 글은 수정/삭제 못 한다" (403)
//  프론트에서 버튼을 숨기는 건 편의일 뿐이고, 진짜 방어선은 이 Service 규칙 —
//  그래서 이 규칙이 깨지지 않는지 테스트로 못 박아두는 것.
// ============================================================
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    PostRepository postRepository;

    @InjectMocks
    PostService postService;

    // DB에서 읽어온 것처럼 흉내 낸 기존 글 (id=1, 작성자 maru)
    private Post existingPost() {
        return new Post(1L, "제목", "내용", "maru", LocalDateTime.of(2026, 7, 14, 12, 0));
    }

    // ---------------- 조회 ----------------

    @Test
    @DisplayName("없는 글을 조회하면 404")
    void getPost_missing_throws404() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPost(99L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("있는 글을 조회하면 내용이 그대로 담겨 나옴")
    void getPost_found_returnsResult() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost()));

        PostResult result = postService.getPost(1L);

        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getAuthor()).isEqualTo("maru");
    }

    // ---------------- 글쓰기 ----------------

    @Test
    @DisplayName("제목이 비어있으면 글쓰기 실패")
    void writePost_blankTitle_fails() {
        assertThatThrownBy(() -> postService.writePost(new WritePostCommand("", "내용", "maru")))
                .isInstanceOf(IllegalArgumentException.class);
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("정상 글쓰기면 저장됨")
    void writePost_success_saves() {
        postService.writePost(new WritePostCommand("제목", "내용", "maru"));

        verify(postRepository).save(any(Post.class));
    }

    // ---------------- 수정 ----------------

    @Test
    @DisplayName("본인 글이면 수정 성공")
    void updatePost_owner_succeeds() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost()));

        postService.updatePost(new UpdatePostCommand(1L, "새 제목", "새 내용", "maru"));

        verify(postRepository).update(1L, "새 제목", "새 내용", "maru");
    }

    @Test
    @DisplayName("남의 글을 수정하려 하면 403")
    void updatePost_notOwner_throws403() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost()));   // 작성자는 maru

        // hacker가 maru의 글을 고치려고 시도
        assertThatThrownBy(() -> postService.updatePost(new UpdatePostCommand(1L, "해킹", "해킹", "hacker")))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.FORBIDDEN);
        // 당연히 UPDATE 쿼리도 실행되면 안 됨
        verify(postRepository, never()).update(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("없는 글을 수정하려 하면 404")
    void updatePost_missing_throws404() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.updatePost(new UpdatePostCommand(99L, "제목", "내용", "maru")))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ---------------- 삭제 ----------------

    @Test
    @DisplayName("본인 글이면 삭제 성공")
    void deletePost_owner_succeeds() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost()));

        postService.deletePost(1L, "maru");

        verify(postRepository).deleteById(1L, "maru");
    }

    @Test
    @DisplayName("남의 글을 삭제하려 하면 403")
    void deletePost_notOwner_throws403() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost()));

        assertThatThrownBy(() -> postService.deletePost(1L, "hacker"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.FORBIDDEN);
        verify(postRepository, never()).deleteById(anyLong(), anyString());
    }
}
