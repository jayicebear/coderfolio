package com.example.coderfolio.contexts.profile.application;

import com.example.coderfolio.contexts.profile.application.dto.AddEducationCommand;
import com.example.coderfolio.contexts.profile.application.dto.ProfileResult;
import com.example.coderfolio.contexts.profile.application.dto.UpdateEducationCommand;
import com.example.coderfolio.contexts.profile.infra.Education;
import com.example.coderfolio.contexts.profile.infra.ProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// ============================================================
//  ProfileServiceTest  -  이력서 규칙 검증
//  학력만 대표로 테스트함 (경력/프로젝트는 완전히 같은 패턴이라 생략 —
//  나중에 규칙이 서로 달라지면 그때 각각 추가하면 됨)
// ============================================================
@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    ProfileRepository profileRepository;

    @InjectMocks
    ProfileService profileService;

    // ---------------- 조회 ----------------

    @Test
    @DisplayName("기본정보를 아직 저장 안 한 사람도 조회는 가능 (이름은 비어있음)")
    void getProfile_noProfileRow_stillWorks() {
        // given: profiles 행 없음 + 학력만 하나 있음
        when(profileRepository.findProfile("maru")).thenReturn(Optional.empty());
        when(profileRepository.findEducations("maru"))
                .thenReturn(List.of(new Education(1L, "maru", "한국대", "컴공", "학사", "2019~2023")));
        when(profileRepository.findCareers("maru")).thenReturn(List.of());
        when(profileRepository.findProjects("maru")).thenReturn(List.of());

        // when
        ProfileResult result = profileService.getProfile("maru");

        // then: NullPointerException 없이, username은 채워지고 name은 빈 값
        assertThat(result.getUsername()).isEqualTo("maru");
        assertThat(result.getName()).isNull();
        assertThat(result.getEducations()).hasSize(1);
    }

    // ---------------- 학력 추가 ----------------

    @Test
    @DisplayName("학교명이 비어있으면 학력 추가 실패")
    void addEducation_blankSchool_fails() {
        assertThatThrownBy(() -> profileService.addEducation(
                new AddEducationCommand("maru", "", "컴공", "학사", "2019~2023")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("학교명");
        verify(profileRepository, never()).addEducation(any());
    }

    @Test
    @DisplayName("정상 입력이면 학력이 저장됨")
    void addEducation_success_saves() {
        profileService.addEducation(new AddEducationCommand("maru", "한국대", "컴공", "학사", "2019~2023"));

        verify(profileRepository).addEducation(any(Education.class));
    }

    // ---------------- 학력 수정 ----------------

    @Test
    @DisplayName("정상 수정이면 성공 (바뀐 줄 1개)")
    void updateEducation_success() {
        when(profileRepository.updateEducation(any(Education.class))).thenReturn(1);

        // 예외 없이 끝나면 성공
        profileService.updateEducation(new UpdateEducationCommand(1L, "maru", "한국대", "소프트웨어", "학사", "2019~2023"));

        verify(profileRepository).updateEducation(any(Education.class));
    }

    @Test
    @DisplayName("없는 항목(또는 남의 항목)을 수정하면 404")
    void updateEducation_notMineOrMissing_throws404() {
        // given: UPDATE 했는데 바뀐 줄이 0개 = 그 id가 없거나 내 것이 아님
        when(profileRepository.updateEducation(any(Education.class))).thenReturn(0);

        assertThatThrownBy(() -> profileService.updateEducation(
                new UpdateEducationCommand(99L, "maru", "한국대", "컴공", "학사", "2019~2023")))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("수정할 때도 학교명이 비어있으면 실패")
    void updateEducation_blankSchool_fails() {
        assertThatThrownBy(() -> profileService.updateEducation(
                new UpdateEducationCommand(1L, "maru", "", "컴공", "학사", "2019~2023")))
                .isInstanceOf(IllegalArgumentException.class);
        verify(profileRepository, never()).updateEducation(any());
    }
}
