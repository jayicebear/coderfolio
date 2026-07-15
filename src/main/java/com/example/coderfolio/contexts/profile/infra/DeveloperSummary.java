package com.example.coderfolio.contexts.profile.infra;

// ============================================================
//  DeveloperSummary  -  "개발자 둘러보기" 목록의 카드 한 장에 필요한 값만 담는 객체
//  이력서 전체(ProfileResult)를 다 내려주면 낭비라서, 목록용으로 요약본만 따로 둠.
//  (name/intro는 아직 기본정보를 저장 안 한 회원이면 null일 수 있음)
// ============================================================
public class DeveloperSummary {

    private final String username;
    private final String name;
    private final String intro;
    private final int careerCount;    // 등록한 경력 개수
    private final int projectCount;   // 등록한 프로젝트 개수

    public DeveloperSummary(String username, String name, String intro, int careerCount, int projectCount) {
        this.username = username;
        this.name = name;
        this.intro = intro;
        this.careerCount = careerCount;
        this.projectCount = projectCount;
    }

    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getIntro() { return intro; }
    public int getCareerCount() { return careerCount; }
    public int getProjectCount() { return projectCount; }
}
