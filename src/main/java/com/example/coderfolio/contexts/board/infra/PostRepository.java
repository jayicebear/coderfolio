package com.example.coderfolio.contexts.board.infra;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

// ============================================================
//  PostRepository  -  JdbcTemplate으로 SQL을 직접 쓰는 게시글 리포지토리
// ============================================================
@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Post> POST_MAPPER = (rs, rowNum) -> new Post(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getString("author"),
            rs.getLong("view_count"),
            rs.getLong("like_count"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    // 조회할 때마다 좋아요 수를 post_likes 줄 수로 세서(서브쿼리) 같이 가져옴
    private static final String SELECT_COLUMNS =
            "SELECT id, title, content, author, view_count, "
            + "(SELECT COUNT(*) FROM post_likes pl WHERE pl.post_id = posts.id) AS like_count, "
            + "created_at FROM posts ";

    public void save(Post post) {
        String sql = "INSERT INTO posts (title, content, author, created_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, post.getTitle(), post.getContent(), post.getAuthor(),
                Timestamp.valueOf(post.getCreatedAt()));
    }

    // 페이지네이션 + 검색: keyword가 있으면 제목/내용에 그 단어가 포함된 글만, limit/offset으로 한 페이지만 잘라서 조회
    // (where 문자열은 우리가 만든 고정 문구일 뿐이고, 검색어 자체는 항상 ? 자리표시자로 넘겨서 SQL 인젝션과 무관함)
    public List<Post> findPage(String keyword, int limit, int offset) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        String where = hasKeyword ? "WHERE title LIKE ? OR content LIKE ? " : "";
        String sql = SELECT_COLUMNS + where + "ORDER BY created_at DESC LIMIT ? OFFSET ?";

        if (hasKeyword) {
            String pattern = "%" + keyword + "%";
            return jdbcTemplate.query(sql, POST_MAPPER, pattern, pattern, limit, offset);
        }
        return jdbcTemplate.query(sql, POST_MAPPER, limit, offset);
    }

    // 검색 조건에 맞는 전체 글 개수 (페이지 수 계산에 필요)
    public long count(String keyword) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        String where = hasKeyword ? "WHERE title LIKE ? OR content LIKE ?" : "";
        String sql = "SELECT COUNT(*) FROM posts " + where;

        if (hasKeyword) {
            String pattern = "%" + keyword + "%";
            return jdbcTemplate.queryForObject(sql, Long.class, pattern, pattern);
        }
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public Optional<Post> findById(Long id) {
        String sql = SELECT_COLUMNS + "WHERE id = ?";
        List<Post> results = jdbcTemplate.query(sql, POST_MAPPER, id);
        return results.stream().findFirst();
    }

    // 수정: 기존 행을 찾아서 제목/내용만 바꿈 (UPDATE).
    // author 조건도 같이 걸어서, SQL 차원에서도 "남의 글"은 절대 못 고치게 이중 안전장치.
    public void update(Long id, String title, String content, String author) {
        String sql = "UPDATE posts SET title = ?, content = ? WHERE id = ? AND author = ?";
        jdbcTemplate.update(sql, title, content, id, author);
    }

    // 삭제: author 조건 포함 (남의 글 삭제 방지)
    public void deleteById(Long id, String author) {
        jdbcTemplate.update("DELETE FROM posts WHERE id = ? AND author = ?", id, author);
    }

    // ---------------- 조회수 ----------------

    // 상세 조회 때마다 +1. "현재값 읽어서 +1 해서 다시 쓰기"가 아니라 DB가 한 번에 처리하게 해서
    // 동시에 여러 명이 봐도 카운트가 누락되지 않음 (원자적 증가)
    public void incrementViewCount(Long id) {
        jdbcTemplate.update("UPDATE posts SET view_count = view_count + 1 WHERE id = ?", id);
    }

    // ---------------- 좋아요 (post_likes) ----------------

    public boolean hasLiked(Long postId, String username) {
        Long found = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM post_likes WHERE post_id = ? AND username = ?",
                Long.class, postId, username);
        return found != null && found > 0;
    }

    public void addLike(Long postId, String username) {
        // UNIQUE 제약(post_id, username) 덕분에 중복으로 눌러도 두 줄이 생기지 않음.
        // INSERT IGNORE: 이미 있으면 에러 대신 조용히 넘어가는 MySQL 문법
        jdbcTemplate.update(
                "INSERT IGNORE INTO post_likes (post_id, username, created_at) VALUES (?, ?, NOW(6))",
                postId, username);
    }

    public void removeLike(Long postId, String username) {
        jdbcTemplate.update("DELETE FROM post_likes WHERE post_id = ? AND username = ?", postId, username);
    }

    public long countLikes(Long postId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM post_likes WHERE post_id = ?", Long.class, postId);
    }
}
