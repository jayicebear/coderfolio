package com.example.coderfolio.contexts.login.infra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// ============================================================
//  LoginAttemptLimiterTest  -  brute-force 방어 규칙 검증
//  Mock 없이 진짜 객체를 씀 (DB 등 외부 의존성이 없는 순수 메모리 로직이라서).
// ============================================================
class LoginAttemptLimiterTest {

    LoginAttemptLimiter limiter = new LoginAttemptLimiter();

    @Test
    @DisplayName("처음 시도하는 아이디는 잠겨있지 않음")
    void newUsername_notLocked() {
        assertThatCode(() -> limiter.checkNotLocked("maru")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("4번 실패까지는 아직 잠기지 않음")
    void fourFailures_stillNotLocked() {
        for (int i = 0; i < 4; i++) {
            limiter.recordFailure("maru");
        }
        assertThatCode(() -> limiter.checkNotLocked("maru")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("5번 연속 실패하면 잠김 (429)")
    void fiveFailures_locksAccount() {
        for (int i = 0; i < 5; i++) {
            limiter.recordFailure("maru");
        }

        assertThatThrownBy(() -> limiter.checkNotLocked("maru"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    }

    @Test
    @DisplayName("로그인 성공하면 실패 기록이 초기화됨")
    void success_resetsFailureCount() {
        for (int i = 0; i < 4; i++) {
            limiter.recordFailure("maru");
        }
        limiter.recordSuccess("maru");

        // 초기화됐으니 다시 4번 실패해도 아직 잠기면 안 됨 (5번째부터 잠김이므로)
        for (int i = 0; i < 4; i++) {
            limiter.recordFailure("maru");
        }
        assertThatCode(() -> limiter.checkNotLocked("maru")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("다른 아이디끼리는 실패 횟수가 서로 섞이지 않음")
    void differentUsernames_areIndependent() {
        for (int i = 0; i < 5; i++) {
            limiter.recordFailure("maru");
        }

        // maru는 잠겼어도 other는 영향 없어야 함
        assertThatCode(() -> limiter.checkNotLocked("other")).doesNotThrowAnyException();
    }
}
