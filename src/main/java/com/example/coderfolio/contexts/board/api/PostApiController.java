package com.example.coderfolio.contexts.board.api;

import com.example.coderfolio.contexts.board.api.dto.PostViewResponse;
import com.example.coderfolio.contexts.board.api.dto.PostWriteApiRequest;
import com.example.coderfolio.contexts.board.application.PostService;
import com.example.coderfolio.contexts.board.application.dto.UpdatePostCommand;
import com.example.coderfolio.contexts.board.application.dto.WritePostCommand;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// ============================================================
//  PostApiController  -  게시판 JSON API
//  - 목록/상세 조회는 누구나 (예전 화면과 동일한 규칙)
//  - 글쓰기는 로그인 필요 (작성자는 세션에서 꺼냄)
//  PostService는 예전 그대로 재사용 — api 계층만 갈아끼운 것.
// ============================================================
@RestController
@RequestMapping("/api/posts")
public class PostApiController {

    private final PostService postService;

    @Autowired
    public PostApiController(PostService postService) {
        this.postService = postService;
    }

    // GET /api/posts  -  글 목록 (최신순). List를 리턴하면 JSON 배열로 내려감
    @GetMapping
    public List<PostViewResponse> list() {
        return postService.getAllPosts().stream().map(PostViewResponse::from).toList();
    }

    // GET /api/posts/{id}  -  글 하나. 없으면 Service가 404를 던짐
    @GetMapping("/{id}")
    public PostViewResponse detail(@PathVariable Long id) {
        return PostViewResponse.from(postService.getPost(id));
    }

    // POST /api/posts  -  글 등록 (로그인 필요). body: { "title": "...", "content": "..." }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void write(@RequestBody PostWriteApiRequest request, HttpSession session) {
        String loginUser = requireLogin(session);
        postService.writePost(new WritePostCommand(request.getTitle(), request.getContent(), loginUser));
    }

    // PUT /api/posts/{id}  -  글 수정 (로그인 + 본인 글만). body는 글쓰기와 동일한 모양
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @RequestBody PostWriteApiRequest request, HttpSession session) {
        String loginUser = requireLogin(session);
        postService.updatePost(new UpdatePostCommand(id, request.getTitle(), request.getContent(), loginUser));
    }

    // DELETE /api/posts/{id}  -  글 삭제 (로그인 + 본인 글만)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, HttpSession session) {
        String loginUser = requireLogin(session);
        postService.deletePost(id, loginUser);
    }

    // 세션에서 로그인 사용자를 꺼내고, 없으면 401 (ProfileApiController와 같은 패턴)
    private String requireLogin(HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요해요.");
        }
        return loginUser;
    }
}
