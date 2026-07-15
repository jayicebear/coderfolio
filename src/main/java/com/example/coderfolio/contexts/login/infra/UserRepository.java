package com.example.coderfolio.contexts.login.infra;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ============================================================
//  UserRepository  -  JdbcTemplate으로 SQL을 "직접" 쓰는 리포지토리
//  Spring Data JPA와 달리, 메서드 이름 규칙이 아니라 우리가 SQL을 눈으로 보고 관리해요.
//  대신 Connection 여닫기, ResultSet 반복 같은 반복 작업은 JdbcTemplate이 대신 해줘요.
// ============================================================
@Repository   // "나는 DB 접근 담당 부품이야" 라고 Spring에게 알려줌 (컴포넌트 스캔 대상이 됨)
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper: "DB 조회 결과 한 줄(ResultSet)을 User 객체 하나로 바꾸는 방법"을 정의
    private static final RowMapper<User> USER_MAPPER = (rs, rowNum) -> new User(
            rs.getLong("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    public void save(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        jdbcTemplate.update(sql, user.getUsername(), user.getPassword());
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password, created_at FROM users WHERE username = ?";
        List<User> results = jdbcTemplate.query(sql, USER_MAPPER, username);
        return results.stream().findFirst();
    }

    // 비밀번호 변경: 이미 암호화된 값을 받아서 그대로 덮어씀 (암호화는 Service가 미리 해서 넘김)
    public void updatePassword(String username, String hashedPassword) {
        jdbcTemplate.update("UPDATE users SET password = ? WHERE username = ?", hashedPassword, username);
    }

    // 회원 탈퇴: 계정 자체를 삭제
    // (posts/comments/profile 등 이 사람이 남긴 다른 데이터는 지우지 않음 - author/username 값으로만
    //  느슨하게 연결되어 있어서, 이 프로젝트의 다른 관계들과 마찬가지로 진짜 FK 제약은 걸지 않은 설계)
    public void deleteByUsername(String username) {
        jdbcTemplate.update("DELETE FROM users WHERE username = ?", username);
    }
}