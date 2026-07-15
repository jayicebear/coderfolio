package com.example.coderfolio.contexts.profile.application.dto;

// ============================================================
//  UpdateProjectCommand  -  프로젝트 한 줄 수정에 필요한 값
// ============================================================
public class UpdateProjectCommand {

    private final Long id;
    private final String username;
    private final String name;
    private final String description;
    private final String techStack;
    private final String period;

    public UpdateProjectCommand(Long id, String username, String name, String description, String techStack, String period) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.description = description;
        this.techStack = techStack;
        this.period = period;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getTechStack() { return techStack; }
    public String getPeriod() { return period; }
}
