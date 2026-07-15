package com.example.coderfolio.contexts.profile.application;

import com.example.coderfolio.contexts.profile.application.dto.AddCareerCommand;
import com.example.coderfolio.contexts.profile.application.dto.AddEducationCommand;
import com.example.coderfolio.contexts.profile.application.dto.AddProjectCommand;
import com.example.coderfolio.contexts.profile.application.dto.DeveloperPageResult;
import com.example.coderfolio.contexts.profile.application.dto.ProfileResult;
import com.example.coderfolio.contexts.profile.application.dto.SaveProfileCommand;
import com.example.coderfolio.contexts.profile.application.dto.UpdateCareerCommand;
import com.example.coderfolio.contexts.profile.application.dto.UpdateEducationCommand;
import com.example.coderfolio.contexts.profile.application.dto.UpdateProjectCommand;
import com.example.coderfolio.contexts.profile.infra.Career;
import com.example.coderfolio.contexts.profile.infra.Education;
import com.example.coderfolio.contexts.profile.infra.Profile;
import com.example.coderfolio.contexts.profile.infra.ProfileRepository;
import com.example.coderfolio.contexts.profile.infra.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// ============================================================
//  ProfileService  -  이력서 규칙(비즈니스 로직) 담당
//  Controller가 넘긴 Command를 받아 저장/조회하고, 조회 결과는 ProfileResult로 묶어 돌려줌.
// ============================================================
@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    private static final int MAX_PAGE_SIZE = 60;

    // 개발자 둘러보기 목록 (최신 가입순, 검색 + 페이지네이션). PostService.getPosts와 같은 패턴
    public DeveloperPageResult getDevelopers(int page, int size, String keyword) {
        int safePage = Math.max(page, 1);
        int safeSize = (size < 1 || size > MAX_PAGE_SIZE) ? 12 : size;
        int offset = (safePage - 1) * safeSize;

        long totalCount = profileRepository.countDevelopers(keyword);
        int totalPages = (int) Math.ceil((double) totalCount / safeSize);

        return new DeveloperPageResult(
                profileRepository.findDevelopers(keyword, safeSize, offset),
                safePage, safeSize, totalCount, totalPages);
    }

    // 한 사람의 이력서 전체(기본정보 + 학력/경력/프로젝트)를 한 번에 조회
    public ProfileResult getProfile(String username) {
        Profile profile = profileRepository.findProfile(username).orElse(null);
        return new ProfileResult(
                username,
                profile,
                profileRepository.findEducations(username),
                profileRepository.findCareers(username),
                profileRepository.findProjects(username)
        );
    }

    // 기본 정보 저장 (있으면 수정, 없으면 새로 생성 = upsert)
    public void saveProfile(SaveProfileCommand cmd) {
        profileRepository.saveProfile(new Profile(cmd.getUsername(), cmd.getName(), cmd.getEmail(), cmd.getIntro()));
    }

    // ---------------- 학력 ----------------
    public void addEducation(AddEducationCommand cmd) {
        if (isBlank(cmd.getSchool())) {
            throw new IllegalArgumentException("학교명을 입력해주세요.");
        }
        profileRepository.addEducation(
                new Education(cmd.getUsername(), cmd.getSchool(), cmd.getMajor(), cmd.getDegree(), cmd.getPeriod()));
    }

    public void updateEducation(UpdateEducationCommand cmd) {
        if (isBlank(cmd.getSchool())) {
            throw new IllegalArgumentException("학교명을 입력해주세요.");
        }
        // 수정된 줄이 0개면 = 그 id의 항목이 없거나 내 것이 아님 → 404
        int updated = profileRepository.updateEducation(new Education(
                cmd.getId(), cmd.getUsername(), cmd.getSchool(), cmd.getMajor(), cmd.getDegree(), cmd.getPeriod()));
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "수정할 항목을 찾을 수 없어요.");
        }
    }

    public void deleteEducation(String username, Long id) {
        profileRepository.deleteEducation(username, id);
    }

    // ---------------- 경력 ----------------
    public void addCareer(AddCareerCommand cmd) {
        if (isBlank(cmd.getCompany())) {
            throw new IllegalArgumentException("회사명을 입력해주세요.");
        }
        profileRepository.addCareer(
                new Career(cmd.getUsername(), cmd.getCompany(), cmd.getPosition(), cmd.getDescription(), cmd.getPeriod()));
    }

    public void updateCareer(UpdateCareerCommand cmd) {
        if (isBlank(cmd.getCompany())) {
            throw new IllegalArgumentException("회사명을 입력해주세요.");
        }
        int updated = profileRepository.updateCareer(new Career(
                cmd.getId(), cmd.getUsername(), cmd.getCompany(), cmd.getPosition(), cmd.getDescription(), cmd.getPeriod()));
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "수정할 항목을 찾을 수 없어요.");
        }
    }

    public void deleteCareer(String username, Long id) {
        profileRepository.deleteCareer(username, id);
    }

    // ---------------- 프로젝트 ----------------
    public void addProject(AddProjectCommand cmd) {
        if (isBlank(cmd.getName())) {
            throw new IllegalArgumentException("프로젝트 이름을 입력해주세요.");
        }
        profileRepository.addProject(
                new Project(cmd.getUsername(), cmd.getName(), cmd.getDescription(), cmd.getTechStack(), cmd.getPeriod()));
    }

    public void updateProject(UpdateProjectCommand cmd) {
        if (isBlank(cmd.getName())) {
            throw new IllegalArgumentException("프로젝트 이름을 입력해주세요.");
        }
        int updated = profileRepository.updateProject(new Project(
                cmd.getId(), cmd.getUsername(), cmd.getName(), cmd.getDescription(), cmd.getTechStack(), cmd.getPeriod()));
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "수정할 항목을 찾을 수 없어요.");
        }
    }

    public void deleteProject(String username, Long id) {
        profileRepository.deleteProject(username, id);
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
