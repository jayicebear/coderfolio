package com.example.coderfolio.contexts.login.application.dto;

// ============================================================
//  DeleteAccountCommand  -  회원 탈퇴에 필요한 값
//  비밀번호를 다시 확인받아서, 세션을 탈취당한 경우가 아니라 "본인이 직접" 탈퇴하는 것인지 재확인함.
// ============================================================
public class DeleteAccountCommand {

    private final String username;
    private final String password;

    public DeleteAccountCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
