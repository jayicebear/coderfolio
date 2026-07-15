package com.example.coderfolio.contexts.profile.application.dto;

import com.example.coderfolio.contexts.profile.infra.Career;
import com.example.coderfolio.contexts.profile.infra.Education;
import com.example.coderfolio.contexts.profile.infra.Profile;
import com.example.coderfolio.contexts.profile.infra.Project;

import java.util.List;

// ============================================================
//  ProfileResult  -  한 사람의 이력서 전체를 하나로 묶어 돌려주는 결과 DTO
//  기본정보(profile) + 학력/경력/프로젝트 목록을 한 번에 담음.
//  화면에서는 읽기만 하므로, 목록 항목은 infra 객체를 그대로 담아 단순화함
//  (Education/Career/Project 는 값만 가진 단순 객체라 별도 Result로 또 감싸지 않음).
// ============================================================
public class ProfileResult {

    private final String username;   // 소유자 (조회 대상)
    private final String name;
    private final String email;
    private final String intro;
    private final List<Education> educations;
    private final List<Career> careers;
    private final List<Project> projects;

    public ProfileResult(String username, Profile profile,
                         List<Education> educations, List<Career> careers, List<Project> projects) {
        this.username = username;
        // profile 행이 아직 없을 수도 있음(기본정보 저장 전) → 그럴 땐 빈 값으로
        this.name = profile != null ? profile.getName() : null;
        this.email = profile != null ? profile.getEmail() : null;
        this.intro = profile != null ? profile.getIntro() : null;
        this.educations = educations;
        this.careers = careers;
        this.projects = projects;
    }

    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getIntro() { return intro; }
    public List<Education> getEducations() { return educations; }
    public List<Career> getCareers() { return careers; }
    public List<Project> getProjects() { return projects; }
}
