package com.example.coderfolio.contexts.login.api.dto;

// ============================================================
//  LoginApiRequest  -  로그인 "화면 폼"에서 오는 입력을 담는 DTO
// ============================================================
public class LoginApiRequest {

    private String username;
    private String password;

    public LoginApiRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}