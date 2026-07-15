package com.example.coderfolio.contexts.login.api;

import com.example.coderfolio.contexts.login.api.dto.LoginApiRequest;
import com.example.coderfolio.contexts.login.api.dto.SignupApiRequest;
import com.example.coderfolio.contexts.login.api.dto.UserResponse;
import com.example.coderfolio.contexts.login.application.UserService;
import com.example.coderfolio.contexts.login.application.dto.LoginCommand;
import com.example.coderfolio.contexts.login.application.dto.LoginResult;
import com.example.coderfolio.contexts.login.application.dto.SignupCommand;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

// ============================================================
//  AuthApiController  -  회원가입/로그인/로그아웃 JSON API
//  예전 AuthController(@Controller)는 HTML 화면을 리턴했지만,
//  이제는 @RestController라서 리턴값이 그대로 JSON이 되어 내려감.
//  화면(html)은 static/ 폴더의 JS가 fetch()로 이 API를 호출해서 그림.
//
//  로그인 상태는 예전과 똑같이 세션(HttpSession)으로 관리함.
//  (같은 서버에서 fetch를 쓰면 세션 쿠키가 자동으로 오가서 JWT 없이도 동작)
// ============================================================
@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final UserService userService;

    @Autowired
    public AuthApiController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/auth/signup  -  회원가입. body: { "username": "...", "password": "..." }
    // 검증 실패(빈 값/중복)는 Service가 IllegalArgumentException을 던지고,
    // GlobalExceptionHandler가 400 + { "message": ... } 로 바꿔서 내려줌.
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)   // 성공하면 201 Created
    public void signup(@RequestBody SignupApiRequest request) {
        userService.register(new SignupCommand(request.getUsername(), request.getPassword()));
    }

    // POST /api/auth/login  -  로그인. 성공하면 세션에 기록하고 { "username": ... } 리턴
    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginApiRequest request, HttpSession session) {
        Optional<LoginResult> result =
                userService.login(new LoginCommand(request.getUsername(), request.getPassword()));
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 틀렸어요.");
        }
        session.setAttribute("loginUser", result.get().getUsername());
        return new UserResponse(result.get().getUsername());
    }

    // POST /api/auth/logout  -  세션 무효화. 성공 시 204 No Content (내용 없음)
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpSession session) {
        session.invalidate();
    }

    // GET /api/auth/me  -  "지금 나 로그인 돼있어?" 확인용.
    // 프론트가 페이지를 열 때마다 이걸 호출해서, 401이면 로그인 화면으로 보냄.
    @GetMapping("/me")
    public UserResponse me(HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요해요.");
        }
        return new UserResponse(loginUser);
    }
}
