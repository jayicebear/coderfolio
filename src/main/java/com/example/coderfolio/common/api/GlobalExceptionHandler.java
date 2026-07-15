package com.example.coderfolio.common.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

// ============================================================
//  GlobalExceptionHandler  -  모든 @RestController의 예외를 한 곳에서 JSON으로 변환
//  예전 MVC에선 컨트롤러마다 try-catch 해서 model.addAttribute("error", ...)를 했지만,
//  API에선 여기서 일괄로 { "message": "..." } + 상태코드로 바꿔서 내려줌.
// ============================================================
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Service가 던지는 검증 실패 (빈 값, 중복 아이디 등) → 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    // 컨트롤러/서비스가 상태코드를 지정해 던진 예외 (401, 404 등) → 그 상태코드 그대로
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleStatus(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(new ErrorResponse(e.getReason()));
    }

    // 그 외 예상 못 한 에러 → 500 (실제 예외 내용은 서버 로그로만, 밖으론 일반 메시지)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("서버에 문제가 발생했어요. 잠시 후 다시 시도해주세요."));
    }
}
