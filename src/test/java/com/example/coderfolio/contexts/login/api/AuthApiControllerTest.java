package com.example.coderfolio.contexts.login.api;

import com.example.coderfolio.contexts.login.application.UserService;
import com.example.coderfolio.contexts.login.application.dto.LoginCommand;
import com.example.coderfolio.contexts.login.application.dto.LoginResult;
import com.example.coderfolio.contexts.login.application.dto.SignupCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// ============================================================
//  AuthApiControllerTest  -  인증 API의 "HTTP 계층" 테스트
//
//  ServiceTest와의 차이:
//  - ServiceTest  : 자바 메서드를 직접 호출해서 "규칙"을 검사
//  - ControllerTest: 진짜 HTTP 요청처럼 URL/JSON/상태코드를 검사
//                    (서버를 진짜 띄우진 않고, MockMvc가 요청-응답만 흉내 냄)
//
//  @WebMvcTest : 이 컨트롤러 하나만 딱 띄우는 "부분" 테스트.
//                DB, 다른 컨트롤러는 안 뜸 → 빠름.
//  @MockitoBean: 진짜 UserService 대신 가짜를 스프링에 등록
//                (컨트롤러는 진짜, 그 아래는 가짜 — HTTP 계층만 검사하려고).
//  GlobalExceptionHandler(@RestControllerAdvice)는 자동으로 같이 떠서,
//  "예외 → 상태코드 + {message}" 변환까지 여기서 함께 검증됨.
// ============================================================
@WebMvcTest(AuthApiController.class)
class AuthApiControllerTest {

    @Autowired
    MockMvc mockMvc;   // 가짜 HTTP 요청을 보내는 도구

    @MockitoBean
    UserService userService;

    // ---------------- 회원가입 ----------------

    @Test
    @DisplayName("회원가입 성공 → 201 Created")
    void signup_success_returns201() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"maru\",\"password\":\"pw1234\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("중복 아이디 회원가입 → 400 + message 필드에 이유가 담김")
    void signup_duplicate_returns400WithMessage() throws Exception {
        // given: Service가 IllegalArgumentException을 던지는 상황
        doThrow(new IllegalArgumentException("이미 존재하는 아이디예요: maru"))
                .when(userService).register(any(SignupCommand.class));

        // then: GlobalExceptionHandler가 400 + {"message": ...} 로 바꿔주는지 확인
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"maru\",\"password\":\"pw1234\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 존재하는 아이디예요: maru"));
    }

    // ---------------- 로그인 ----------------

    @Test
    @DisplayName("로그인 성공 → 200 + username 리턴 + 세션에 기록됨")
    void login_success_returns200AndSetsSession() throws Exception {
        when(userService.login(any(LoginCommand.class)))
                .thenReturn(Optional.of(new LoginResult("maru")));

        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/api/auth/login")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"maru\",\"password\":\"pw1234\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("maru"));

        // 응답뿐 아니라 "세션에 로그인 기록이 남았는지"도 확인
        assertThat(session.getAttribute("loginUser")).isEqualTo("maru");
    }

    @Test
    @DisplayName("비밀번호가 틀리면 → 401 + 에러 메시지")
    void login_wrongPassword_returns401() throws Exception {
        when(userService.login(any(LoginCommand.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"maru\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 틀렸어요."));
    }

    // ---------------- 내 정보 확인 (me) ----------------

    @Test
    @DisplayName("로그인 상태에서 /me → 200 + username")
    void me_loggedIn_returns200() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginUser", "maru");   // 로그인된 세션을 흉내

        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("maru"));
    }

    @Test
    @DisplayName("로그인 안 한 상태에서 /me → 401")
    void me_notLoggedIn_returns401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    // ---------------- 로그아웃 ----------------

    @Test
    @DisplayName("로그아웃 → 204 + 세션 무효화")
    void logout_returns204() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginUser", "maru");

        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isNoContent());

        assertThat(session.isInvalid()).isTrue();   // 세션이 실제로 파기됐는지
    }
}
