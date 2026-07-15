package com.example.coderfolio.contexts.profile.application.dto;

// ============================================================
//  AddCareerCommand  -  경력 한 줄 추가에 필요한 값
// ============================================================
public class AddCareerCommand {

    private final String username;
    private final String company;
    private final String position;
    private final String description;
    private final String period;

    public AddCareerCommand(String username, String company, String position, String description, String period) {
        this.username = username;
        this.company = company;
        this.position = position;
        this.description = description;
        this.period = period;
    }

    public String getUsername() { return username; }
    public String getCompany() { return company; }
    public String getPosition() { return position; }
    public String getDescription() { return description; }
    public String getPeriod() { return period; }
}
