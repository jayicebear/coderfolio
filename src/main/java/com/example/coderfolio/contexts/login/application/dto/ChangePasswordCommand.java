package com.example.coderfolio.contexts.login.application.dto;

// ============================================================
//  ChangePasswordCommand  -  비밀번호 변경에 필요한 값
//  username은 세션에서 오므로 Controller가 채워 넘김.
// ============================================================
public class ChangePasswordCommand {

    private final String username;
    private final String currentPassword;
    private final String newPassword;

    public ChangePasswordCommand(String username, String currentPassword, String newPassword) {
        this.username = username;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getUsername() { return username; }
    public String getCurrentPassword() { return currentPassword; }
    public String getNewPassword() { return newPassword; }
}
