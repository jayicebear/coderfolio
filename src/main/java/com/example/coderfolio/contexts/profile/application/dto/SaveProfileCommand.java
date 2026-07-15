package com.example.coderfolio.contexts.profile.application.dto;

// ============================================================
//  SaveProfileCommand  -  기본 정보 저장에 필요한 값
//  username 은 화면 폼이 아니라 세션(로그인 정보)에서 오므로 Controller가 채워 넘김.
// ============================================================
public class SaveProfileCommand {

    private final String username;
    private final String name;
    private final String email;
    private final String intro;

    public SaveProfileCommand(String username, String name, String email, String intro) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.intro = intro;
    }

    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getIntro() { return intro; }
}
