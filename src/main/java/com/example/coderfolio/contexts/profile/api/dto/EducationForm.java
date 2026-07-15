package com.example.coderfolio.contexts.profile.api.dto;

// ============================================================
//  EducationForm  -  학력 추가 폼에서 오는 입력
// ============================================================
public class EducationForm {

    private String school;
    private String major;
    private String degree;
    private String period;

    public EducationForm() {}

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
}
