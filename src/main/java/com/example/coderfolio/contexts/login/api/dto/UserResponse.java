package com.example.coderfolio.contexts.login.api.dto;

// ============================================================
//  UserResponse  -  "지금 로그인한 사용자" 정보를 JSON으로 내려줄 때의 모양
//  예: { "username": "maru" }
// ============================================================
public class UserResponse {

    private final String username;

    public UserResponse(String username) {
        this.username = username;
    }

    public String getUsername() { return username; }
}
