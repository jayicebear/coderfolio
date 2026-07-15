package com.example.coderfolio.contexts.login.infra;

// ============================================================
//  LoginAttemptLimiter  -  brute-force 방어의 "약속(인터페이스)"
//
//  PasswordEncoder(인터페이스) <- BCryptPasswordEncoder(구현) 와 똑같은 패턴.
//  "무엇을 할 수 있어야 하는가"(메서드 3개)만 여기서 정하고,
//  "어떻게 하는가"(메모리에 저장? Redis에 저장?)는 구현 클래스가 정함.
//
//  현재 구현: InMemoryLoginAttemptLimiter (서버 메모리에 기록, 서버 1대일 때 충분)
//  나중에 서버가 여러 대로 늘어나면: RedisLoginAttemptLimiter 같은 구현을 새로 만들어
//  갈아끼우면 되고, 이 인터페이스를 쓰는 UserService는 한 줄도 안 바꿔도 됨.
// ============================================================
public interface LoginAttemptLimiter {

    // 로그인을 시도하기 전에 호출: 잠겨있으면 예외(429)를 던져서 그 자리에서 막음
    void checkNotLocked(String username);

    // 로그인 실패할 때마다 호출: 실패 횟수를 늘리고, 기준을 넘으면 잠금
    void recordFailure(String username);

    // 로그인 성공하면 호출: 그동안의 실패 기록을 전부 지움
    void recordSuccess(String username);
}
