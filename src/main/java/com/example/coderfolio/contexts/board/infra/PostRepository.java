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
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    public void save(Post post) {
        String sql = "INSERT INTO posts (title, content, author, created_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, post.getTitle(), post.getContent(), post.getAuthor(),
                Timestamp.valueOf(post.getCreatedAt()));
    }

    public List<Post> findAllByOrderByCreatedAtDesc() {
        String sql = "SELECT id, title, content, author, created_at FROM posts ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, POST_MAPPER);
    }

    public Optional<Post> findById(Long id) {
        String sql = "SELECT id, title, content, author, created_at FROM posts WHERE id = ?";
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
}