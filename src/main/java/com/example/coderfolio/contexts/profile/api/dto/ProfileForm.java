package com.example.coderfolio.contexts.profile.api.dto;

// ============================================================
//  ProfileForm  -  기본 정보 편집 폼에서 오는 입력
// ============================================================
public class ProfileForm {

    private String name;
    private String email;
    private String intro;

    public ProfileForm() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }
}
