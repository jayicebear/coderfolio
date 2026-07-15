package com.example.coderfolio.contexts.profile.infra;

// ============================================================
//  Career  -  경력 한 줄 (username 당 여러 개 가능)
// ============================================================
public class Career {

    private Long id;
    private String username;
    private String company;      // 회사
    private String position;     // 직책/직무
    private String description;  // 담당 업무 설명
    private String period;       // 재직 기간

    public Career(String username, String company, String position, String description, String period) {
        this.username = username;
        this.company = company;
        this.position = position;
        this.description = description;
        this.period = period;
    }

    public Career(Long id, String username, String company, String position, String description, String period) {
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
