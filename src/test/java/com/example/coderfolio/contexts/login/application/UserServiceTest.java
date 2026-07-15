package com.example.coderfolio.contexts.login.application;

import com.example.coderfolio.contexts.login.application.dto.ChangePasswordCommand;
import com.example.coderfolio.contexts.login.application.dto.DeleteAccountCommand;
import com.example.coderfolio.contexts.login.application.dto.LoginCommand;
import com.example.coderfolio.contexts.login.application.dto.LoginResult;
import com.example.coderfolio.contexts.login.application.dto.SignupCommand;
import com.example.coderfolio.contexts.login.infra.LoginAttemptLimiter;
import com.example.coderfolio.contexts.login.infra.User;
import com.example.coderfolio.contexts.login.infra.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// ============================================================
//  UserServiceTest  -  UserService의 "규칙"이 지켜지는지 검증하는 단위 테스트
//
//  단위 테스트 = 진짜 DB 없이, Service 하나만 떼어놓고 검사.
//  @Mock : 진짜 UserRepository 대신 "가짜"를 만들어 끼움.
//          가짜라서 DB 연결 없이도 "이렇게 응답해라"를 마음대로 정할 수 있음(when).
//  @InjectMocks : 위에서 만든 가짜들을 UserService 생성자에 자동으로 꽂아줌.
//
//  각 테스트는 given(상황 준비) → when(실행) → then(결과 확인) 3단계로 읽으면 됨.
// ============================================================
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    LoginAttemptLimiter loginAttemptLimiter;

    @InjectMocks
    UserService userService;

    // ---------------- 회원가입 ----------------

    @Test
    @DisplayName("아이디가 비어있으면 회원가입 실패")
    void register_blankUsername_fails() {
        assertThatThrownBy(() -> userService.register(new SignupCommand("", "pw1234")))
                .isInstanceOf(IllegalArgumentException.class);
        // 검증 실패했으니 DB 저장은 절대 일어나면 안 됨
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("비밀번호가 비어있으면 회원가입 실패")
    void register_blankPassword_fails() {
        assertThatThrownBy(() -> userService.register(new SignupCommand("maru", "")))
                .isInstanceOf(IllegalArgumentException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 존재하는 아이디면 회원가입 실패")
    void register_duplicateUsername_fails() {
        // given: 가짜 리포지토리가 "maru는 이미 있다"고 답하게 만듦
        when(userRepository.findByUsername("maru"))
                .thenReturn(Optional.of(new User("maru", "hashed")));

        // when + then
        assertThatThrownBy(() -> userService.register(new SignupCommand("maru", "pw1234")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 아이디");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("정상 회원가입이면 비밀번호를 암호화해서 저장 (원문 저장 금지)")
    void register_success_savesEncodedPassword() {
        // given: 중복 없음 + 인코더가 "pw1234"를 "ENCODED"로 바꿔준다고 가정
        when(userRepository.findByUsername("maru")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pw1234")).thenReturn("ENCODED");

        // when
        userService.register(new SignupCommand("maru", "pw1234"));

        // then: save에 넘어간 User를 붙잡아서(captor) 안의 값을 직접 확인
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("maru");
        assertThat(captor.getValue().getPassword()).isEqualTo("ENCODED");   // 원문 "pw1234"면 안 됨!
    }

    // ---------------- 로그인 ----------------

    @Test
    @DisplayName("아이디와 비밀번호가 맞으면 로그인 성공")
    void login_correctPassword_succeeds() {
        when(userRepository.findByUsername("maru"))
                .thenReturn(Optional.of(new User("maru", "ENCODED")));
        when(passwordEncoder.matches("pw1234", "ENCODED")).thenReturn(true);

        Optional<LoginResult> result = userService.login(new LoginCommand("maru", "pw1234"));

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("maru");
    }

    @Test
    @DisplayName("비밀번호가 틀리면 로그인 실패")
    void login_wrongPassword_fails() {
        when(userRepository.findByUsername("maru"))
                .thenReturn(Optional.of(new User("maru", "ENCODED")));
        when(passwordEncoder.matches("wrong", "ENCODED")).thenReturn(false);

        Optional<LoginResult> result = userService.login(new LoginCommand("maru", "wrong"));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("없는 아이디면 로그인 실패")
    void login_unknownUser_fails() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        Optional<LoginResult> result = userService.login(new LoginCommand("ghost", "pw1234"));

        assertThat(result).isEmpty();
    }

    // ---------------- 로그인 시도 제한 (brute-force 방어) ----------------

    @Test
    @DisplayName("로그인 성공하면 limiter에 성공을 기록하고, 실패 기록은 안 남김")
    void login_success_recordsSuccessOnLimiter() {
        when(userRepository.findByUsername("maru"))
                .thenReturn(Optional.of(new User("maru", "ENCODED")));
        when(passwordEncoder.matches("pw1234", "ENCODED")).thenReturn(true);

        userService.login(new LoginCommand("maru", "pw1234"));

        verify(loginAttemptLimiter).recordSuccess("maru");
        verify(loginAttemptLimiter, never()).recordFailure(any());
    }

    @Test
    @DisplayName("로그인 실패하면 limiter에 실패를 기록함")
    void login_failure_recordsFailureOnLimiter() {
        when(userRepository.findByUsername("maru"))
                .thenReturn(Optional.of(new User("maru", "ENCODED")));
        when(passwordEncoder.matches("wrong", "ENCODED")).thenReturn(false);

        userService.login(new LoginCommand("maru", "wrong"));

        verify(loginAttemptLimiter).recordFailure("maru");
    }

    @Test
    @DisplayName("limiter가 잠긴 상태로 판단하면, 비밀번호를 맞춰봐도 429가 그대로 전달됨 (DB 조회 자체를 안 함)")
    void login_lockedByLimiter_throws429WithoutTouchingDb() {
        doThrow(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "너무 많이 시도했어요."))
                .when(loginAttemptLimiter).checkNotLocked("maru");

        assertThatThrownBy(() -> userService.login(new LoginCommand("maru", "pw1234")))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.TOO_MANY_REQUESTS);

        verify(userRepository, never()).findByUsername(any());
    }

    // ---------------- 비밀번호 변경 ----------------

    @Test
    @DisplayName("새 비밀번호가 비어있으면 실패")
    void changePassword_blankNewPassword_fails() {
        assertThatThrownBy(() -> userService.changePassword(
                new ChangePasswordCommand("maru", "old1234", "")))
                .isInstanceOf(IllegalArgumentException.class);
        verify(userRepository, never()).updatePassword(any(), any());
    }

    @Test
    @DisplayName("현재 비밀번호가 틀리면 401")
    void changePassword_wrongCurrentPassword_throws401() {
        when(userRepository.findByUsername("maru")).thenReturn(Optional.of(new User("maru", "ENCODED")));
        when(passwordEncoder.matches("wrong", "ENCODED")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(
                new ChangePasswordCommand("maru", "wrong", "new1234")))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(userRepository, never()).updatePassword(any(), any());
    }

    @Test
    @DisplayName("현재 비밀번호가 맞으면 새 비밀번호를 암호화해서 저장")
    void changePassword_success_savesEncodedNewPassword() {
        when(userRepository.findByUsername("maru")).thenReturn(Optional.of(new User("maru", "ENCODED_OLD")));
        when(passwordEncoder.matches("old1234", "ENCODED_OLD")).thenReturn(true);
        when(passwordEncoder.encode("new1234")).thenReturn("ENCODED_NEW");

        userService.changePassword(new ChangePasswordCommand("maru", "old1234", "new1234"));

        verify(userRepository).updatePassword("maru", "ENCODED_NEW");
    }

    // ---------------- 회원 탈퇴 ----------------

    @Test
    @DisplayName("비밀번호가 틀리면 탈퇴 실패 (401)")
    void deleteAccount_wrongPassword_throws401() {
        when(userRepository.findByUsername("maru")).thenReturn(Optional.of(new User("maru", "ENCODED")));
        when(passwordEncoder.matches("wrong", "ENCODED")).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteAccount(new DeleteAccountCommand("maru", "wrong")))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(userRepository, never()).deleteByUsername(any());
    }

    @Test
    @DisplayName("비밀번호가 맞으면 계정이 삭제됨")
    void deleteAccount_correctPassword_deletesUser() {
        when(userRepository.findByUsername("maru")).thenReturn(Optional.of(new User("maru", "ENCODED")));
        when(passwordEncoder.matches("pw1234", "ENCODED")).thenReturn(true);

        userService.deleteAccount(new DeleteAccountCommand("maru", "pw1234"));

        verify(userRepository).deleteByUsername("maru");
    }
}
