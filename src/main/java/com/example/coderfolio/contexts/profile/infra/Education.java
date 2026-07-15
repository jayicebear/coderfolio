package com.example.coderfolio.contexts.profile.infra;

// ============================================================
//  Education  -  학력/전공 한 줄 (username 당 여러 개 가능)
// ============================================================
public class Education {

    private Long id;
    private String username;
    private String school;   // 학교
    private String major;    // 전공
    private String degree;   // 학위 (학사/석사 등)
    private String period;   // 재학 기간 (예: "2019.03 ~ 2023.02")

    public Education(String username, String school, String major, String degree, String period) {
        this.username = username;
        this.school = school;
        this.major = major;
        this.degree = degree;
        this.period = period;
    }

    public Education(Long id, String username, String school, String major, String degree, String period) {
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
