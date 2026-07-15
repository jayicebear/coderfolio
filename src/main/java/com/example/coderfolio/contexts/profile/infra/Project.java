package com.example.coderfolio.contexts.profile.infra;

// ============================================================
//  Project  -  프로젝트 한 줄 (username 당 여러 개 가능)
// ============================================================
public class Project {

    private Long id;
    private String username;
    private String name;         // 프로젝트 이름
    private String description;  // 설명
    private String techStack;    // 사용 기술 (예: "Spring, MySQL")
    private String period;       // 진행 기간

    public Project(String username, String name, String description, String techStack, String period) {
        this.username = username;
        this.name = name;
        this.description = description;
        this.techStack = techStack;
        this.period = period;
    }

    public Project(Long id, String username, String name, String description, String techStack, String period) {
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
