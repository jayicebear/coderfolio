package com.example.coderfolio.contexts.login.application.dto;

// ============================================================
//  LoginResult  -  로그인 성공 시 application 계층이 돌려주는 결과
//  User 엔티티(비밀번호 포함)를 밖으로 안 내보내고, username만 담아서 줌.
// ============================================================
public class LoginResult {

    private final String username;

    public LoginResult(String username) {
        this.username = username;
    }

    public String getUsername() { return username; }
}