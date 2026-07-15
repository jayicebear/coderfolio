package com.example.coderfolio.contexts.login.application.dto;

// ============================================================
//  SignupCommand  -  회원가입 "업무 처리"에 필요한 값만 담는 DTO
//  api 계층의 SignupApiRequest와 지금은 필드가 똑같아 보이지만,
//  이 둘은 "역할"이 달라요: 하나는 화면 입력용, 하나는 업무 로직용.
//  나중에 화면 쪽에 필드가 추가돼도(예: 약관 동의 체크박스) 이 클래스는 안 바뀔 수 있어요.
// ============================================================
public class SignupCommand {

    private final String username;
    private final String password;

    public SignupCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}