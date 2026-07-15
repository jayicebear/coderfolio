package com.example.coderfolio.contexts.login.infra;

import java.time.LocalDateTime;

// ============================================================
//  User  -  순수 자바 객체 (POJO). @Entity 같은 JPA 표시가 하나도 없어요.
//  DB와 연결되는 마법이 없으니, UserRepository가 직접 SQL로 이 객체를
//  만들고(조회) / 이 객체의 값을 꺼내서(저장) DB에 넣어줘야 해요.
// ============================================================
public class User {

    private Long id;
    private String username;
    private String password;
    private LocalDateTime createdAt;

    // 회원가입할 때 쓰는 생성자 (아직 id, createdAt은 DB가 정해줄 값이라 없음)
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // DB에서 읽어온 값을 채울 때 쓰는 생성자
    public User(Long id, String username, String password, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
