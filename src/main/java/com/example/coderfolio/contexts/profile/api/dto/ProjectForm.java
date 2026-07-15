package com.example.coderfolio.contexts.profile.api.dto;

// ============================================================
//  ProjectForm  -  프로젝트 추가 폼에서 오는 입력
// ============================================================
public class ProjectForm {

    private String name;
    private String description;
    private String techStack;
    private String period;

    public ProjectForm() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTechStack() { return techStack; }
    public void setTechStack(String techStack) { this.techStack = techStack; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
}
