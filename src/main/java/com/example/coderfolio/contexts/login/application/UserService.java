package com.example.coderfolio.contexts.login.application;

import com.example.coderfolio.contexts.login.application.dto.LoginCommand;
import com.example.coderfolio.contexts.login.application.dto.LoginResult;
import com.example.coderfolio.contexts.login.application.dto.SignupCommand;
import com.example.coderfolio.contexts.login.infra.User;
import com.example.coderfolio.contexts.login.infra.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
    public Optional<LoginResult> login(LoginCommand command) {
        Optional<User> found = userRepository.findByUsername(command.getUsername());
        if (found.isPresent() && passwordEncoder.matches(command.getPassword(), found.get().getPassword())) {
            return Optional.of(new LoginResult(found.get().getUsername()));
        }
        return Optional.empty();
    }
}