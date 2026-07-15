package com.example.coderfolio.contexts.profile.api;

import com.example.coderfolio.contexts.profile.application.ProfileService;
import com.example.coderfolio.contexts.profile.application.dto.DeveloperPageResult;
import com.example.coderfolio.contexts.profile.application.dto.ProfileResult;
import com.example.coderfolio.contexts.profile.infra.DeveloperSummary;
import com.example.coderfolio.contexts.profile.infra.Education;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// ============================================================
//  ProfileApiControllerTest  -  이력서 API의 HTTP 계층 테스트
//  핵심 확인 사항:
//  - 조회(GET)는 로그인 없이 누구나 가능 (공개 포트폴리오)
//  - /me 로 시작하는 편집 경로는 전부 로그인 필요(401)
// ============================================================
@WebMvcTest(ProfileApiController.class)
class ProfileApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ProfileService profileService;

    private MockHttpSession loginSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginUser", "maru");
        return session;
    }

    // ---------------- 개발자 둘러보기 ----------------

    @Test
    @DisplayName("개발자 목록은 로그인 없이 조회 가능 → 200 + 페이지 정보")
    void listDevelopers_withoutLogin_returns200() throws Exception {
        when(profileService.getDevelopers(1, 12, null)).thenReturn(new DeveloperPageResult(
                List.of(new DeveloperSummary("maru", "김철수", "안녕하세요", 2, 3)), 1, 12, 1L, 1));

        mockMvc.perform(get("/api/profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.developers[0].username").value("maru"))
                .andExpect(jsonPath("$.developers[0].careerCount").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("page/size/keyword 파라미터가 Service로 그대로 전달됨")
    void listDevelopers_withQueryParams_passesThemToService() throws Exception {
        when(profileService.getDevelopers(2, 6, "자바")).thenReturn(
                new DeveloperPageResult(List.of(), 2, 6, 0L, 0));

        mockMvc.perform(get("/api/profiles?page=2&size=6&keyword=자바"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2));
    }

    // ---------------- 공개 조회 ----------------

    @Test
    @DisplayName("이력서 조회는 로그인 없이 가능 → 200 + 이력서 JSON")
    void view_withoutLogin_returns200() throws Exception {
        when(profileService.getProfile("maru")).thenReturn(new ProfileResult(
                "maru",
                null,   // 기본정보 저장 전이어도
                List.of(new Education(1L, "maru", "한국대", "컴공", "학사", "2019~2023")),
                List.of(),
                List.of()));

        mockMvc.perform(get("/api/profiles/maru"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("maru"))
                .andExpect(jsonPath("$.educations[0].school").value("한국대"))
                .andExpect(jsonPath("$.careers").isEmpty());
    }

    // ---------------- 편집은 로그인 필수 ----------------

    @Test
    @DisplayName("로그인 없이 기본정보 저장 → 401")
    void saveProfile_withoutLogin_returns401() throws Exception {
        mockMvc.perform(put("/api/profiles/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"김철수\",\"email\":\"a@b.com\",\"intro\":\"hi\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 후 기본정보 저장 → 204")
    void saveProfile_withLogin_returns204() throws Exception {
        mockMvc.perform(put("/api/profiles/me")
                        .session(loginSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"김철수\",\"email\":\"a@b.com\",\"intro\":\"hi\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("로그인 후 학력 추가 → 201")
    void addEducation_withLogin_returns201() throws Exception {
        mockMvc.perform(post("/api/profiles/me/educations")
                        .session(loginSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"school\":\"한국대\",\"major\":\"컴공\",\"degree\":\"학사\",\"period\":\"2019~2023\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("로그인 후 학력 수정 → 204")
    void updateEducation_withLogin_returns204() throws Exception {
        mockMvc.perform(put("/api/profiles/me/educations/1")
                        .session(loginSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"school\":\"한국대\",\"major\":\"소프트웨어\",\"degree\":\"학사\",\"period\":\"2019~2023\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("로그인 없이 학력 삭제 → 401")
    void deleteEducation_withoutLogin_returns401() throws Exception {
        mockMvc.perform(delete("/api/profiles/me/educations/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 후 학력 삭제 → 204")
    void deleteEducation_withLogin_returns204() throws Exception {
        mockMvc.perform(delete("/api/profiles/me/educations/1").session(loginSession()))
                .andExpect(status().isNoContent());
    }
}
