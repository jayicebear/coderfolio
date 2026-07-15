package com.example.coderfolio.contexts.profile.application.dto;

// ============================================================
//  AddProjectCommand  -  프로젝트 한 줄 추가에 필요한 값
// ============================================================
public class AddProjectCommand {

    private final String username;
    private final String name;
    private final String description;
    private final String techStack;
    private final String period;

    public AddProjectCommand(String username, String name, String description, String techStack, String period) {
        this.username = username;
        this.name = name;
        this.description = description;
        this.techStack = techStack;
        this.period = period;
    }

    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getTechStack() { return techStack; }
    public String getPeriod() { return period; }
}
