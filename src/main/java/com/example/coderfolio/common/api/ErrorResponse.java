package com.example.coderfolio.common.api;

// ============================================================
//  ErrorResponse  -  API가 에러를 돌려줄 때의 JSON 모양
//  예: { "message": "이미 존재하는 아이디예요: maru" }
//  프론트(JS)는 응답이 실패면 이 message를 꺼내서 화면에 보여줌.
// ============================================================
public class ErrorResponse {

    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
}
