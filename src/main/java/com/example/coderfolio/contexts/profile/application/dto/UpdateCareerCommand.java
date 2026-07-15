package com.example.coderfolio.contexts.profile.application.dto;

// ============================================================
//  UpdateCareerCommand  -  경력 한 줄 수정에 필요한 값
// ============================================================
public class UpdateCareerCommand {

    private final Long id;
    private final String username;
    private final String company;
    private final String position;
    private final String description;
    private final String period;

    public UpdateCareerCommand(Long id, String username, String company, String position, String description, String period) {
        this.id = id;
        this.username = username;
        this.company = company;
        this.position = position;
        this.description = description;
        this.period = period;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getCompany() { return company; }
    public String getPosition() { return position; }
    public String getDescription() { return description; }
    public String getPeriod() { return period; }
}
