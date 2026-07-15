package com.example.coderfolio.contexts.board.api;

import com.example.coderfolio.contexts.board.application.PostService;
import com.example.coderfolio.contexts.board.application.dto.PostResult;
import com.example.coderfolio.contexts.board.application.dto.UpdatePostCommand;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// ============================================================
//  PostApiControllerTest  -  게시판 API의 HTTP 계층 테스트
//  핵심 확인 사항:
//  - 조회는 로그인 없이 됨 / 쓰기·수정·삭제는 로그인 필요(401)
//  - Service가 던진 403(남의 글)이 HTTP 응답까지 그대로 전달되는지
// ============================================================
@WebMvcTest(PostApiController.class)
class PostApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PostService postService;

    // 로그인된 세션 흉내
    private MockHttpSession loginSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginUser", "maru");
        return session;
    }

    // ---------------- 조회 (로그인 불필요) ----------------

    @Test
    @DisplayName("글 목록은 로그인 없이 조회 가능 → 200 + JSON 배열")
    void list_withoutLogin_returns200() throws Exception {
        when(postService.getAllPosts()).thenReturn(List.of(
                new PostResult(1L, "제목", "내용", "maru", LocalDateTime.of(2026, 7, 14, 12, 0))));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("제목"))
                .andExpect(jsonPath("$[0].author").value("maru"));
    }

    // ---------------- 글쓰기 ----------------

    @Test
    @DisplayName("로그인 없이 글쓰기 → 401")
    void write_withoutLogin_returns401() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"제목\",\"content\":\"내용\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 후 글쓰기 → 201")
    void write_withLogin_returns201() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .session(loginSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"제목\",\"content\":\"내용\"}"))
                .andExpect(status().isCreated());
    }

    // ---------------- 수정 / 삭제 ----------------

    @Test
    @DisplayName("본인 글 수정 → 204")
    void update_own_returns204() throws Exception {
        mockMvc.perform(put("/api/posts/1")
                        .session(loginSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"새 제목\",\"content\":\"새 내용\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("남의 글 수정 시 Service의 403이 HTTP 응답까지 전달됨")
    void update_notOwner_returns403() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 쓴 글만 수정할 수 있어요."))
                .when(postService).updatePost(any(UpdatePostCommand.class));

        mockMvc.perform(put("/api/posts/1")
                        .session(loginSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"해킹\",\"content\":\"해킹\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("본인이 쓴 글만 수정할 수 있어요."));
    }

    @Test
    @DisplayName("로그인 없이 삭제 → 401")
    void delete_withoutLogin_returns401() throws Exception {
        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("본인 글 삭제 → 204")
    void delete_own_returns204() throws Exception {
        mockMvc.perform(delete("/api/posts/1").session(loginSession()))
                .andExpect(status().isNoContent());
    }
}
