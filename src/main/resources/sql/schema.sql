-- ============================================================
--  schema.sql  -  이 프로젝트가 필요로 하는 테이블 구조를 코드로 남겨둔 파일
--  Spring Boot가 서버 켤 때 자동으로 이 파일을 읽어서 실행해줌.
--  (IF NOT EXISTS라서, 테이블이 이미 있으면 아무 일도 안 하고 넘어감 - 기존 데이터 안전)
-- ============================================================

-- (DB 자체는 spring.datasource.url이 이미 login_app을 가리키고 있어서 여기선 테이블만 정의)

CREATE TABLE IF NOT EXISTS users (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  username   VARCHAR(50)  NOT NULL UNIQUE,
  password   VARCHAR(255) NOT NULL,
  created_at DATETIME     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS posts (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  title      VARCHAR(200) NOT NULL,
  content    TEXT         NOT NULL,
  author     VARCHAR(50)  NOT NULL,
  created_at DATETIME(6)
);

-- ============================================================
--  경력(이력서) 관련 테이블
--  한 사용자(username)당 profiles 1줄 + educations/careers/projects 는 여러 줄.
--  username 을 기준으로 묶임 (users.username 과 연결되는 개념).
-- ============================================================

-- 기본 정보: username 당 딱 하나 (UNIQUE) → 저장 시 있으면 UPDATE, 없으면 INSERT
CREATE TABLE IF NOT EXISTS profiles (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  username   VARCHAR(50)  NOT NULL UNIQUE,
  name       VARCHAR(100),
  email      VARCHAR(150),
  intro      TEXT,
  updated_at DATETIME(6)
);

-- 학력 + 전공 (여러 개)
CREATE TABLE IF NOT EXISTS educations (
  id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50)  NOT NULL,
  school   VARCHAR(150) NOT NULL,
  major    VARCHAR(150),
  degree   VARCHAR(50),
  period   VARCHAR(50)
);

-- 경력 (여러 개)
CREATE TABLE IF NOT EXISTS careers (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  username    VARCHAR(50)  NOT NULL,
  company     VARCHAR(150) NOT NULL,
  position    VARCHAR(100),
  description TEXT,
  period      VARCHAR(50)
);

-- 프로젝트 (여러 개)
CREATE TABLE IF NOT EXISTS projects (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  username    VARCHAR(50)  NOT NULL,
  name        VARCHAR(150) NOT NULL,
  description TEXT,
  tech_stack  VARCHAR(255),
  period      VARCHAR(50)
);