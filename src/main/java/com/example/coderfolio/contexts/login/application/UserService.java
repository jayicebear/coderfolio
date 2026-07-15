package com.example.coderfolio.contexts.login.application;

import com.example.coderfolio.contexts.login.application.dto.ChangePasswordCommand;
import com.example.coderfolio.contexts.login.application.dto.DeleteAccountCommand;
import com.example.coderfolio.contexts.login.application.dto.LoginCommand;
import com.example.coderfolio.contexts.login.application.dto.LoginResult;
import com.example.coderfolio.contexts.login.application.dto.SignupCommand;
import com.example.coderfolio.contexts.login.infra.LoginAttemptLimiter;
import com.example.coderfolio.contexts.login.infra.User;
import com.example.coderfolio.contexts.login.infra.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

// ============================================================
//  UserService  -  규칙(비즈니스 로직) 담당
//  비밀번호는 여기서 "암호화해서 저장" / "암호화된 것끼리 비교"를 함.
//  DB엔 원본 비밀번호가 절대 안 들어가고, 여기서 만든 해시값만 들어감.
//  암호화 방식(BCrypt) 자체는 infra.security.PasswordEncoderConfig에서 빈으로 등록.
// ============================================================
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptLimiter loginAttemptLimiter;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                        LoginAttemptLimiter loginAttemptLimiter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptLimiter = loginAttemptLimiter;
    }

    // 회원가입 규칙: 빈 값 금지 + 중복 아이디 금지 + 비밀번호는 암호화해서 저장
    public void register(SignupCommand command) {
        String username = command.getUsername();
        String rawPassword = command.getPassword();

        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("아이디와 비밀번호를 모두 입력해주세요.");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디예요: " + username);
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);   // 여기서 암호화!
        userRepository.save(new User(username, hashedPassword));
    }

    // 로그인: 입력한 비밀번호(원문)와 저장된 해시값을 passwordEncoder가 비교해줌
    // brute-force 방어: 시도 전에 잠겨있는지부터 확인하고, 실패/성공 결과를 limiter에 기록함
    public Optional<LoginResult> login(LoginCommand command) {
        String username = command.getUsername();
        loginAttemptLimiter.checkNotLocked(username);   // 잠겨있으면 여기서 예외 던지고 끝 (DB 조회 안 함)

        Optional<User> found = userRepository.findByUsername(username);
        boolean success = found.isPresent()
                && passwordEncoder.matches(command.getPassword(), found.get().getPassword());

        if (success) {
            loginAttemptLimiter.recordSuccess(username);
            return Optional.of(new LoginResult(found.get().getUsername()));
        }
        loginAttemptLimiter.recordFailure(username);
        return Optional.empty();
    }

    // 비밀번호 변경: 현재 비밀번호가 맞는지 먼저 확인하고, 맞으면 새 비밀번호를 암호화해서 덮어씀
    public void changePassword(ChangePasswordCommand command) {
        if (command.getNewPassword() == null || command.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("새 비밀번호를 입력해주세요.");
        }

        User user = findUserOrThrow(command.getUsername());
        if (!passwordEncoder.matches(command.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "현재 비밀번호가 틀렸어요.");
        }

        userRepository.updatePassword(command.getUsername(), passwordEncoder.encode(command.getNewPassword()));
    }

    // 회원 탈퇴: 비밀번호를 다시 확인해서 본인 의사임을 검증한 뒤 계정 삭제
    public void deleteAccount(DeleteAccountCommand command) {
        User user = findUserOrThrow(command.getUsername());
        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸어요.");
        }

        userRepository.deleteByUsername(command.getUsername());
    }

    private User findUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없어요."));
    }
}