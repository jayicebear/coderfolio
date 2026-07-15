package com.example.coderfolio.contexts.login.infra;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// ============================================================
//  InMemoryLoginAttemptLimiter  -  LoginAttemptLimiter 인터페이스의 "메모리 구현체"
//  (BCryptPasswordEncoder가 PasswordEncoder를 구현하는 것과 같은 관계)
//
//  아이디별 실패 횟수를 서버 메모리(ConcurrentHashMap)에 기록:
//  - 서버를 재시작하면 기록이 초기화됨 (학습 프로젝트 규모엔 충분)
//  - 서버가 여러 대로 늘어나면 서버마다 따로 세게 되므로,
//    그때는 Redis 등 공유 저장소를 쓰는 새 구현체로 교체하면 됨
// ============================================================
@Component   // 스프링이 이 구현체를 만들어서, LoginAttemptLimiter 타입을 요구하는 곳(UserService)에 꽂아줌
public class InMemoryLoginAttemptLimiter implements LoginAttemptLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(5);

    // 아이디 하나당 "실패 횟수 + 잠긴 시각"을 담는 기록
    private static class Attempt {
        int failCount;
        Instant lockedUntil;   // null이면 아직 잠기지 않은 상태
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    // 로그인을 시도하기 전에 호출: 잠겨있으면 예외를 던져서 그 자리에서 막음
    // (비밀번호가 맞는지 확인하기도 전에 차단하는 것이 핵심 — DB 조회조차 안 함)
    @Override
    public synchronized void checkNotLocked(String username) {
        Attempt attempt = attempts.get(username);
        if (attempt == null || attempt.lockedUntil == null) {
            return;
        }
        if (Instant.now().isBefore(attempt.lockedUntil)) {
            long minutesLeft = Duration.between(Instant.now(), attempt.lockedUntil).toMinutes() + 1;
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "로그인 시도가 너무 많아요. " + minutesLeft + "분 후 다시 시도해주세요.");
        }
        // 잠금 시간이 지났으면 기록을 지워서 다시 처음부터 셀 수 있게 함
        attempts.remove(username);
    }

    // 로그인 실패할 때마다 호출: 실패 횟수를 늘리고, 기준을 넘으면 잠금
    @Override
    public synchronized void recordFailure(String username) {
        Attempt attempt = attempts.computeIfAbsent(username, key -> new Attempt());
        attempt.failCount++;
        if (attempt.failCount >= MAX_ATTEMPTS) {
            attempt.lockedUntil = Instant.now().plus(LOCK_DURATION);
        }
    }

    // 로그인 성공하면 호출: 그동안의 실패 기록을 전부 지움
    @Override
    public synchronized void recordSuccess(String username) {
        attempts.remove(username);
    }
}
