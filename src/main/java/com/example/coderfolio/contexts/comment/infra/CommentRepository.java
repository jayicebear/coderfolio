package com.example.coderfolio.contexts.comment.infra;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

// ============================================================
//  CommentRepository  -  JdbcTemplate으로 SQL을 직접 쓰는 댓글 리포지토리
//  PostRepository와 완전히 같은 패턴 (save / findById / findByPostId / deleteById).
// ============================================================
@Repository
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Comment> COMMENT_MAPPER = (rs, rowNum) -> new Comment(
            rs.getLong("id"),
            rs.getLong("post_id"),
            rs.getString("author"),
            rs.getString("content"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    public void save(Comment comment) {
        String sql = "INSERT INTO comments (post_id, author, content, created_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, comment.getPostId(), comment.getAuthor(), comment.getContent(),
                Timestamp.valueOf(comment.getCreatedAt()));
    }

    // 한 글에 달린 댓글 전부 (오래된 순 - 대화 흐름 그대로 보이게)
    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT id, post_id, author, content, created_at FROM comments WHERE post_id = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, COMMENT_MAPPER, postId);
    }

    public Optional<Comment> findById(Long id) {
        String sql = "SELECT id, post_id, author, content, created_at FROM comments WHERE id = ?";
        List<Comment> results = jdbcTemplate.query(sql, COMMENT_MAPPER, id);
        return results.stream().findFirst();
    }

    // author 조건 포함 (남의 댓글 삭제 방지 - posts와 동일한 이중 안전장치)
    public void deleteById(Long id, String author) {
        jdbcTemplate.update("DELETE FROM comments WHERE id = ? AND author = ?", id, author);
    }
}
