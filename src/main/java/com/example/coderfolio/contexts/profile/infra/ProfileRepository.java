package com.example.coderfolio.contexts.profile.infra;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

// ============================================================
//  ProfileRepository  -  이력서(기본정보 + 학력/경력/프로젝트) DB 접근 담당
//  profiles/educations/careers/projects 4개 테이블이 하나의 "이력서"를 이루므로
//  UserRepository/PostRepository 처럼 나누지 않고 여기 한 곳에서 다룸.
// ============================================================
@Repository
public class ProfileRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProfileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ---------------- RowMapper: DB 한 줄 -> 자바 객체 ----------------

    private static final RowMapper<Profile> PROFILE_MAPPER = (rs, n) -> new Profile(
            rs.getLong("id"), rs.getString("username"), rs.getString("name"),
            rs.getString("email"), rs.getString("intro"),
            rs.getTimestamp("updated_at").toLocalDateTime()
    );

    private static final RowMapper<Education> EDUCATION_MAPPER = (rs, n) -> new Education(
            rs.getLong("id"), rs.getString("username"), rs.getString("school"),
            rs.getString("major"), rs.getString("degree"), rs.getString("period")
    );

    private static final RowMapper<Career> CAREER_MAPPER = (rs, n) -> new Career(
            rs.getLong("id"), rs.getString("username"), rs.getString("company"),
            rs.getString("position"), rs.getString("description"), rs.getString("period")
    );

    private static final RowMapper<Project> PROJECT_MAPPER = (rs, n) -> new Project(
            rs.getLong("id"), rs.getString("username"), rs.getString("name"),
            rs.getString("description"), rs.getString("tech_stack"), rs.getString("period")
    );

    // ---------------- 개발자 둘러보기 목록 ----------------

    private static final RowMapper<DeveloperSummary> DEVELOPER_MAPPER = (rs, n) -> new DeveloperSummary(
            rs.getString("username"), rs.getString("name"), rs.getString("intro"),
            rs.getInt("career_count"), rs.getInt("project_count")
    );

    // 가입한 개발자 목록 (최신 가입순).
    // users 를 기준으로 profiles 를 LEFT JOIN — 기본정보를 아직 저장 안 한 새 회원도 목록에 나오게 함.
    // (users 테이블은 login 도메인 소유지만, 자바 코드가 아니라 SQL 차원의 조회라서
    //  도메인 간 클래스 참조 없이 값만 읽는 이 프로젝트의 연결 방식과 일관됨)
    public List<DeveloperSummary> findDevelopers(String keyword, int limit, int offset) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        String where = hasKeyword ? "WHERE u.username LIKE ? OR p.name LIKE ? OR p.intro LIKE ? " : "";
        String sql = "SELECT u.username, p.name, p.intro, "
                + "(SELECT COUNT(*) FROM careers c WHERE c.username = u.username) AS career_count, "
                + "(SELECT COUNT(*) FROM projects pr WHERE pr.username = u.username) AS project_count "
                + "FROM users u LEFT JOIN profiles p ON p.username = u.username "
                + where + "ORDER BY u.created_at DESC LIMIT ? OFFSET ?";

        if (hasKeyword) {
            String pattern = "%" + keyword + "%";
            return jdbcTemplate.query(sql, DEVELOPER_MAPPER, pattern, pattern, pattern, limit, offset);
        }
        return jdbcTemplate.query(sql, DEVELOPER_MAPPER, limit, offset);
    }

    // 검색 조건에 맞는 전체 개발자 수 (페이지 수 계산용)
    public long countDevelopers(String keyword) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        String where = hasKeyword ? "WHERE u.username LIKE ? OR p.name LIKE ? OR p.intro LIKE ?" : "";
        String sql = "SELECT COUNT(*) FROM users u LEFT JOIN profiles p ON p.username = u.username " + where;

        if (hasKeyword) {
            String pattern = "%" + keyword + "%";
            return jdbcTemplate.queryForObject(sql, Long.class, pattern, pattern, pattern);
        }
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    // ---------------- 기본 정보 (profiles) ----------------

    // 있으면 UPDATE, 없으면 INSERT (username 이 UNIQUE 라서 가능한 MySQL 문법)
    public void saveProfile(Profile p) {
        String sql = "INSERT INTO profiles (username, name, email, intro, updated_at) VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE name = VALUES(name), email = VALUES(email), "
                + "intro = VALUES(intro), updated_at = VALUES(updated_at)";
        jdbcTemplate.update(sql, p.getUsername(), p.getName(), p.getEmail(), p.getIntro(),
                Timestamp.valueOf(p.getUpdatedAt()));
    }

    public Optional<Profile> findProfile(String username) {
        String sql = "SELECT * FROM profiles WHERE username = ?";
        return jdbcTemplate.query(sql, PROFILE_MAPPER, username).stream().findFirst();
    }

    // ---------------- 학력 (educations) ----------------

    public void addEducation(Education e) {
        String sql = "INSERT INTO educations (username, school, major, degree, period) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, e.getUsername(), e.getSchool(), e.getMajor(), e.getDegree(), e.getPeriod());
    }

    public List<Education> findEducations(String username) {
        String sql = "SELECT * FROM educations WHERE username = ? ORDER BY id DESC";
        return jdbcTemplate.query(sql, EDUCATION_MAPPER, username);
    }

    // 수정: id + username 둘 다 조건에 넣어서 본인 항목만 고칠 수 있음.
    // 리턴값은 "바뀐 줄 수" — 0이면 그런 항목이 없거나 남의 것 (Service가 404 처리에 사용)
    public int updateEducation(Education e) {
        String sql = "UPDATE educations SET school = ?, major = ?, degree = ?, period = ? "
                + "WHERE id = ? AND username = ?";
        return jdbcTemplate.update(sql, e.getSchool(), e.getMajor(), e.getDegree(), e.getPeriod(),
                e.getId(), e.getUsername());
    }

    // username 도 조건에 넣어서 "남의 항목"을 지우지 못하게 막음
    public void deleteEducation(String username, Long id) {
        jdbcTemplate.update("DELETE FROM educations WHERE id = ? AND username = ?", id, username);
    }

    // ---------------- 경력 (careers) ----------------

    public void addCareer(Career c) {
        String sql = "INSERT INTO careers (username, company, position, description, period) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, c.getUsername(), c.getCompany(), c.getPosition(), c.getDescription(), c.getPeriod());
    }

    public List<Career> findCareers(String username) {
        String sql = "SELECT * FROM careers WHERE username = ? ORDER BY id DESC";
        return jdbcTemplate.query(sql, CAREER_MAPPER, username);
    }

    public int updateCareer(Career c) {
        String sql = "UPDATE careers SET company = ?, position = ?, description = ?, period = ? "
                + "WHERE id = ? AND username = ?";
        return jdbcTemplate.update(sql, c.getCompany(), c.getPosition(), c.getDescription(), c.getPeriod(),
                c.getId(), c.getUsername());
    }

    public void deleteCareer(String username, Long id) {
        jdbcTemplate.update("DELETE FROM careers WHERE id = ? AND username = ?", id, username);
    }

    // ---------------- 프로젝트 (projects) ----------------

    public void addProject(Project p) {
        String sql = "INSERT INTO projects (username, name, description, tech_stack, period) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, p.getUsername(), p.getName(), p.getDescription(), p.getTechStack(), p.getPeriod());
    }

    public List<Project> findProjects(String username) {
        String sql = "SELECT * FROM projects WHERE username = ? ORDER BY id DESC";
        return jdbcTemplate.query(sql, PROJECT_MAPPER, username);
    }

    public int updateProject(Project p) {
        String sql = "UPDATE projects SET name = ?, description = ?, tech_stack = ?, period = ? "
                + "WHERE id = ? AND username = ?";
        return jdbcTemplate.update(sql, p.getName(), p.getDescription(), p.getTechStack(), p.getPeriod(),
                p.getId(), p.getUsername());
    }

    public void deleteProject(String username, Long id) {
        jdbcTemplate.update("DELETE FROM projects WHERE id = ? AND username = ?", id, username);
    }
}
