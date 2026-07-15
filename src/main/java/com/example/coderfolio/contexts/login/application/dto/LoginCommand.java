package com.example.coderfolio.contexts.login.application.dto;

// ============================================================
//  LoginCommand  -  로그인 "업무 처리"에 필요한 값만 담는 DTO
// ============================================================
public class LoginCommand {

    private final String username;
    private final String password;

    public LoginCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}