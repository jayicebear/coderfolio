package com.example.coderfolio.contexts.comment.api;

import com.example.coderfolio.contexts.comment.application.CommentService;
import com.example.coderfolio.contexts.comment.application.dto.CommentResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// ============================================================
//  CommentApiControllerTest  -  댓글 API의 HTTP 계층 테스트
// ============================================================
@WebMvcTest(CommentApiController.class)
class CommentApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CommentService commentService;

    private MockHttpSession loginSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginUser", "maru");
        return session;
    }

    @Test
    @DisplayName("댓글 목록은 로그인 없이 조회 가능 → 200 + JSON 배열")
    void list_withoutLogin_returns200() throws Exception {
        when(commentService.getComments(10L)).thenReturn(List.of(
                new CommentResult(1L, 10L, "maru", "댓글 내용", LocalDateTime.of(2026, 7, 15, 12, 0))));

        mockMvc.perform(get("/api/posts/10/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("maru"))
                .andExpect(jsonPath("$[0].content").value("댓글 내용"));
    }

    @Test
    @DisplayName("로그인 없이 댓글 작성 → 401")
    void write_withoutLogin_returns401() throws Exception {
        mockMvc.perform(post("/api/posts/10/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"댓글\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 후 댓글 작성 → 201")
    void write_withLogin_returns201() throws Exception {
        mockMvc.perform(post("/api/posts/10/comments")
                        .session(loginSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"댓글\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("로그인 없이 댓글 삭제 → 401")
    void delete_withoutLogin_returns401() throws Exception {
        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("본인 댓글 삭제 → 204")
    void delete_own_returns204() throws Exception {
        mockMvc.perform(delete("/api/comments/1").session(loginSession()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("남의 댓글 삭제 시 Service의 403이 HTTP 응답까지 전달됨")
    void delete_notOwner_returns403() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 쓴 댓글만 삭제할 수 있어요."))
                .when(commentService).deleteComment(anyLong(), anyString());

        mockMvc.perform(delete("/api/comments/1").session(loginSession()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("본인이 쓴 댓글만 삭제할 수 있어요."));
    }
}
