package com.example.coderfolio.contexts.profile.api;

import com.example.coderfolio.contexts.profile.api.dto.CareerForm;
import com.example.coderfolio.contexts.profile.api.dto.EducationForm;
import com.example.coderfolio.contexts.profile.api.dto.ProfileForm;
import com.example.coderfolio.contexts.profile.api.dto.ProjectForm;
import com.example.coderfolio.contexts.profile.application.ProfileService;
import com.example.coderfolio.contexts.profile.application.dto.AddCareerCommand;
import com.example.coderfolio.contexts.profile.application.dto.AddEducationCommand;
import com.example.coderfolio.contexts.profile.application.dto.AddProjectCommand;
import com.example.coderfolio.contexts.profile.application.dto.ProfileResult;
import com.example.coderfolio.contexts.profile.application.dto.SaveProfileCommand;
import com.example.coderfolio.contexts.profile.application.dto.UpdateCareerCommand;
import com.example.coderfolio.contexts.profile.application.dto.UpdateEducationCommand;
import com.example.coderfolio.contexts.profile.application.dto.UpdateProjectCommand;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// ============================================================
//  ProfileApiController  -  경력(이력서) JSON API
//  - GET /api/profiles/{username} : 누구나 조회 (공개 포트폴리오)
//  - /me 로 시작하는 경로        : 로그인한 본인만 (세션 확인)
//  HTTP 메서드로 의도를 표현: 조회=GET, 새 항목=POST, 통째 저장=PUT, 삭제=DELETE
// ============================================================
@RestController
@RequestMapping("/api/profiles")
public class ProfileApiController {

    private final ProfileService profileService;

    @Autowired
    public ProfileApiController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // ================= 공개 조회 =================

    // GET /api/profiles/{username}  -  이력서 전체(기본정보+학력+경력+프로젝트)를 JSON 하나로
    @GetMapping("/{username}")
    public ProfileResult view(@PathVariable String username) {
        return profileService.getProfile(username);
    }

    // ================= 본인 편집 =================

    // PUT /api/profiles/me  -  기본 정보 저장 (있으면 수정, 없으면 생성이라 PUT이 어울림)
    @PutMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveProfile(@RequestBody ProfileForm form, HttpSession session) {
        String loginUser = requireLogin(session);
        profileService.saveProfile(
                new SaveProfileCommand(loginUser, form.getName(), form.getEmail(), form.getIntro()));
    }

    // ----- 학력 -----
    @PostMapping("/me/educations")
    @ResponseStatus(HttpStatus.CREATED)
    public void addEducation(@RequestBody EducationForm form, HttpSession session) {
        String loginUser = requireLogin(session);
        profileService.addEducation(new AddEducationCommand(
                loginUser, form.getSchool(), form.getMajor(), form.getDegree(), form.getPeriod()));
    }

    // PUT /api/profiles/me/educations/{id}  -  학력 한 줄 수정
    @PutMapping("/me/educations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEducation(@PathVariable Long id, @RequestBody EducationForm form, HttpSession session) {
        String loginUser = requireLogin(session);
        profileService.updateEducation(new UpdateEducationCommand(
                id, loginUser, form.getSchool(), form.getMajor(), form.getDegree(), form.getPeriod()));
    }

    @DeleteMapping("/me/educations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEducation(@PathVariable Long id, HttpSession session) {
        profileService.deleteEducation(requireLogin(session), id);
    }

    // ----- 경력 -----
    @PostMapping("/me/careers")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCareer(@RequestBody CareerForm form, HttpSession session) {
        String loginUser = requireLogin(session);
        profileService.addCareer(new AddCareerCommand(
                loginUser, form.getCompany(), form.getPosition(), form.getDescription(), form.getPeriod()));
    }

    // PUT /api/profiles/me/careers/{id}  -  경력 한 줄 수정
    @PutMapping("/me/careers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCareer(@PathVariable Long id, @RequestBody CareerForm form, HttpSession session) {
        String loginUser = requireLogin(session);
        profileService.updateCareer(new UpdateCareerCommand(
                id, loginUser, form.getCompany(), form.getPosition(), form.getDescription(), form.getPeriod()));
    }

    @DeleteMapping("/me/careers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCareer(@PathVariable Long id, HttpSession session) {
        profileService.deleteCareer(requireLogin(session), id);
    }

    // ----- 프로젝트 -----
    @PostMapping("/me/projects")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProject(@RequestBody ProjectForm form, HttpSession session) {
        String loginUser = requireLogin(session);
        profileService.addProject(new AddProjectCommand(
                loginUser, form.getName(), form.getDescription(), form.getTechStack(), form.getPeriod()));
    }

    // PUT /api/profiles/me/projects/{id}  -  프로젝트 한 줄 수정
    @PutMapping("/me/projects/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProject(@PathVariable Long id, @RequestBody ProjectForm form, HttpSession session) {
        String loginUser = requireLogin(session);
        profileService.updateProject(new UpdateProjectCommand(
                id, loginUser, form.getName(), form.getDescription(), form.getTechStack(), form.getPeriod()));
    }

    @DeleteMapping("/me/projects/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id, HttpSession session) {
        profileService.deleteProject(requireLogin(session), id);
    }

    // 세션에서 로그인 사용자를 꺼내고, 없으면 401을 던짐 (GlobalExceptionHandler가 JSON으로 변환)
    private String requireLogin(HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요해요.");
        }
        return loginUser;
    }
}
