package com.example.coderfolio.contexts.profile.application.dto;

// ============================================================
//  UpdateEducationCommand  -  학력 한 줄 수정에 필요한 값
//  AddEducationCommand와 같은 내용 + "어느 항목인지" 가리키는 id
// ============================================================
public class UpdateEducationCommand {

    private final Long id;
    private final String username;
    private final String school;
    private final String major;
    private final String degree;
    private final String period;

    public UpdateEducationCommand(Long id, String username, String school, String major, String degree, String period) {
        this.id = id;
        this.username = username;
        this.school = school;
        this.major = major;
        this.degree = degree;
        this.period = period;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getSchool() { return school; }
    public String getMajor() { return major; }
    public String getDegree() { return degree; }
    public String getPeriod() { return period; }
}
