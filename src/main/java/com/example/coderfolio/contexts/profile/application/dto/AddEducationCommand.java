package com.example.coderfolio.contexts.profile.application.dto;

// ============================================================
//  AddEducationCommand  -  학력 한 줄 추가에 필요한 값
// ============================================================
public class AddEducationCommand {

    private final String username;
    private final String school;
    private final String major;
    private final String degree;
    private final String period;

    public AddEducationCommand(String username, String school, String major, String degree, String period) {
        this.username = username;
        this.school = school;
        this.major = major;
        this.degree = degree;
        this.period = period;
    }

    public String getUsername() { return username; }
    public String getSchool() { return school; }
    public String getMajor() { return major; }
    public String getDegree() { return degree; }
    public String getPeriod() { return period; }
}
