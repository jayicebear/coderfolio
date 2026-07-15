# coderfolio

개발자를 위한 로그인 · 게시판 · 포트폴리오(이력서) 관리 서비스입니다.
Spring Boot 기반 REST API 서버 + 순수 HTML/JS 프론트엔드로 구성되어 있습니다.

## 주요 기능

### 로그인
- 회원가입 / 로그인 / 로그아웃 (세션 기반)
- 비밀번호는 BCrypt로 암호화하여 저장
- 로그인 시도 제한 (5회 연속 실패 시 5분 잠금, brute-force 방어)
- 비밀번호 변경 / 회원 탈퇴 (본인 확인을 위해 비밀번호 재입력 필요)

### 게시판
- 글 목록 / 상세 조회 (비로그인도 가능), 페이지네이션 + 제목·내용 검색
- 글쓰기 / 수정 / 삭제 (로그인 필요, 본인 글만 수정·삭제 가능)
- 댓글 작성 / 삭제 (로그인 필요, 본인 댓글만 삭제 가능)
- 조회수 (상세 조회 시 자동 증가) + 좋아요 토글 (한 사람당 글 하나에 한 번)

### 포트폴리오 (이력서)
- 기본 정보(이름, 이메일, 자기소개) 등록·수정
- 학력 / 경력 / 프로젝트를 각각 여러 건 추가·수정·삭제
- `/profile.html?user=아이디` 주소로 누구나 조회 가능한 공개 포트폴리오
- 개발자 둘러보기: 가입한 개발자 카드 목록 (검색 + 페이지네이션)

## 기술 스택

| 영역 | 기술 |
|---|---|
| 언어 / 프레임워크 | Java 21, Spring Boot 4.1 |
| DB 접근 | Spring JDBC (`JdbcTemplate`) — JPA 미사용 |
| DB | MySQL |
| 인증 | 세션(`HttpSession`) + BCrypt |
| 프론트엔드 | 순수 HTML/CSS/JS (`fetch` 기반 SPA 형태) |
| 테스트 | JUnit 5, Mockito, `@WebMvcTest` |
| 빌드 | Gradle |

## 프로젝트 구조

계층형(Layered) + 도메인 단위로 구성되어 있습니다.

```
src/main/java/com/example/coderfolio
├── CoderfolioApplication.java
├── common/api          # 전역 예외 처리 (GlobalExceptionHandler)
└── contexts
    ├── login            # 회원가입/로그인
    ├── board            # 게시판
    └── profile          # 포트폴리오(이력서)
```

각 도메인은 아래 3계층으로 나뉩니다.

```
api/          컨트롤러, 화면(요청/응답) 전용 DTO
application/  비즈니스 규칙(Service), Command/Result DTO
infra/        DB 접근(Repository), 엔티티(POJO)
```

프론트엔드는 `src/main/resources/static/`에 있으며, 서버는 JSON API만 제공하고 화면은 브라우저의 JS가 `fetch`로 그립니다.

## 실행 방법

### 1. 사전 준비
- Java 21
- MySQL (로컬에서 실행 중이어야 함)

### 2. 환경변수 설정

프로젝트 루트에 `.env` 파일을 만들고 DB 접속 정보를 입력합니다. (`.env.example` 참고, `.env`는 git에 올라가지 않습니다.)

```
DB_USERNAME=root
DB_PASSWORD=본인의_MySQL_비밀번호
```

### 3. 서버 실행

```bash
./gradlew bootRun
```

서버가 뜨면 브라우저에서 `http://localhost:8080` 접속합니다. 테이블은 `src/main/resources/sql/schema.sql`이 최초 실행 시 자동으로 생성합니다.

## API 개요

| Method | 경로 | 설명 | 인증 |
|---|---|---|---|
| POST | `/api/auth/signup` | 회원가입 | - |
| POST | `/api/auth/login` | 로그인 | - |
| POST | `/api/auth/logout` | 로그아웃 | 필요 |
| GET | `/api/auth/me` | 로그인 상태 확인 | 필요 |
| PUT | `/api/auth/password` | 비밀번호 변경 | 필요 |
| DELETE | `/api/auth/me` | 회원 탈퇴 | 필요 |
| GET | `/api/posts` | 글 목록 | - |
| GET | `/api/posts/{id}` | 글 상세 | - |
| POST | `/api/posts` | 글쓰기 | 필요 |
| PUT | `/api/posts/{id}` | 글 수정 (본인만) | 필요 |
| DELETE | `/api/posts/{id}` | 글 삭제 (본인만) | 필요 |
| POST | `/api/posts/{id}/like` | 좋아요 토글 | 필요 |
| GET | `/api/profiles` | 개발자 둘러보기 목록 (검색/페이지네이션) | - |
| GET | `/api/profiles/{username}` | 포트폴리오 조회 | - |
| PUT | `/api/profiles/me` | 기본 정보 저장 | 필요 |
| POST/PUT/DELETE | `/api/profiles/me/{educations\|careers\|projects}` | 학력/경력/프로젝트 추가·수정·삭제 | 필요 |
| GET | `/api/posts/{postId}/comments` | 댓글 목록 | - |
| POST | `/api/posts/{postId}/comments` | 댓글 작성 | 필요 |
| DELETE | `/api/comments/{id}` | 댓글 삭제 (본인만) | 필요 |

모든 에러 응답은 `{ "message": "..." }` 형태의 JSON과 상태 코드(400/401/403/404)로 통일되어 있습니다.

## 테스트

```bash
./gradlew test
```

- Service 단위 테스트: 회원가입/로그인 규칙, 게시글 소유권 검증(403), 이력서 항목 검증 등
- Controller 테스트(`@WebMvcTest`): 인증 여부에 따른 상태 코드, 예외 → JSON 변환 검증

## 앞으로 할 일

- [ ] Repository(SQL) 계층 테스트 (Testcontainers)
- [x] 로그인 시도 제한 (brute-force 방어)
- [x] 게시판 페이지네이션 / 검색
- [x] 댓글 기능
- [x] 비밀번호 변경 / 회원 탈퇴
