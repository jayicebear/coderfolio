package com.example.coderfolio.contexts.login.api.dto;

// ============================================================
//  DeleteAccountApiRequest  -  회원 탈퇴 폼에서 오는 입력 (본인 확인용 비밀번호)
// ============================================================
public class DeleteAccountApiRequest {

    private String password;

    public DeleteAccountApiRequest() {}

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
