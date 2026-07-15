package com.example.coderfolio.contexts.board.application;

import com.example.coderfolio.contexts.board.application.dto.PostResult;
import com.example.coderfolio.contexts.board.application.dto.UpdatePostCommand;
import com.example.coderfolio.contexts.board.application.dto.WritePostCommand;
import com.example.coderfolio.contexts.board.infra.Post;
import com.example.coderfolio.contexts.board.infra.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// ============================================================
//  PostService  -  게시판 규칙 담당
//  api 계층의 DTO는 전혀 모름. application 전용 Command/Result로만 대화함.
// ============================================================
@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 전체 글 목록 (최신순)
    public List<PostResult> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PostResult::from)
                .toList();
    }

    // 글 하나 조회. 없으면 404 에러
    public PostResult getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없어요."));
        return PostResult.from(post);
    }

    // 글쓰기 규칙: 제목/내용 빈값 금지
    public void writePost(WritePostCommand command) {
        if (command.getTitle() == null || command.getTitle().isBlank()
                || command.getContent() == null || command.getContent().isBlank()) {
            throw new IllegalArgumentException("제목과 내용을 모두 입력해주세요.");
        }
        postRepository.save(new Post(command.getTitle(), command.getContent(), command.getAuthor()));
    }

    // 글 수정 규칙: 빈값 금지 + 글이 있어야 함 + "본인 글"만 가능
    public void updatePost(UpdatePostCommand command) {
        if (command.getTitle() == null || command.getTitle().isBlank()
                || command.getContent() == null || command.getContent().isBlank()) {
            throw new IllegalArgumentException("제목과 내용을 모두 입력해주세요.");
        }
        Post post = postRepository.findById(command.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없어요."));
        if (!post.getAuthor().equals(command.getAuthor())) {
            // 403 Forbidden: "누군지는 알겠는데(로그인은 했는데), 이 글에 대한 권한이 없다"
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 쓴 글만 수정할 수 있어요.");
        }
        postRepository.update(command.getId(), command.getTitle(), command.getContent(), command.getAuthor());
    }

    // 글 삭제 규칙: 글이 있어야 함 + "본인 글"만 가능
    public void deletePost(Long id, String requester) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없어요."));
        if (!post.getAuthor().equals(requester)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 쓴 글만 삭제할 수 있어요.");
        }
        postRepository.deleteById(id, requester);
    }
}