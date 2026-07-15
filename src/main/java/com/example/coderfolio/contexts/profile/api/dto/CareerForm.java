package com.example.coderfolio.contexts.profile.api.dto;

// ============================================================
//  CareerForm  -  경력 추가 폼에서 오는 입력
// ============================================================
public class CareerForm {

    private String company;
    private String position;
    private String description;
    private String period;

    public CareerForm() {}

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
}
