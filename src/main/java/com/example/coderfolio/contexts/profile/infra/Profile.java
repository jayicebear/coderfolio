package com.example.coderfolio.contexts.profile.infra;

import java.time.LocalDateTime;

// ============================================================
//  Profile  -  이력서 "기본 정보" POJO (username 당 하나)
//  User/Post 와 마찬가지로 JPA 표시 없는 순수 자바 객체.
// ============================================================
public class Profile {

    private Long id;
    private String username;   // 소유자 (users.username)
    private String name;       // 이름
    private String email;      // 이메일
    private String intro;      // 자기소개
    private LocalDateTime updatedAt;

    // 새로 저장할 때 쓰는 생성자
    public Profile(String username, String name, String email, String intro) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.intro = intro;
        this.updatedAt = LocalDateTime.now();
    }

    // DB에서 읽어온 값을 채울 때 쓰는 생성자
    public Profile(Long id, String username, String name, String email, String intro, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.intro = intro;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getIntro() { return intro; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
