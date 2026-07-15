package com.example.coderfolio.contexts.login.api.dto;

// ============================================================
//  SignupApiRequest  -  회원가입 "화면 폼"에서 오는 입력을 담는 DTO
//  api 계층 전용. application 계층은 이 클래스를 몰라도 됨.
// ============================================================
public class SignupApiRequest {

    private String username;
    private String password;

    public SignupApiRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}