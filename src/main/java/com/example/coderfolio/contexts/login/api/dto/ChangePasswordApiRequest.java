package com.example.coderfolio.contexts.login.api.dto;

// ============================================================
//  ChangePasswordApiRequest  -  비밀번호 변경 폼에서 오는 입력
// ============================================================
public class ChangePasswordApiRequest {

    private String currentPassword;
    private String newPassword;

    public ChangePasswordApiRequest() {}

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
