package com.example.coderfolio.contexts.board.application;

import com.example.coderfolio.contexts.board.application.dto.LikeResult;
import com.example.coderfolio.contexts.board.application.dto.PostPageResult;
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

    private static final int MAX_PAGE_SIZE = 100;

    // 글 목록 (페이지네이션 + 검색). keyword가 비어있으면 전체 대상으로 검색한 것과 동일하게 동작
    public PostPageResult getPosts(int page, int size, String keyword) {
        int safePage = Math.max(page, 1);
        int safeSize = (size < 1 || size > MAX_PAGE_SIZE) ? 10 : size;
        int offset = (safePage - 1) * safeSize;

        List<PostResult> posts = postRepository.findPage(keyword, safeSize, offset)
                .stream()
                .map(PostResult::from)
                .toList();
        long totalCount = postRepository.count(keyword);
        int totalPages = (int) Math.ceil((double) totalCount / safeSize);

        return new PostPageResult(posts, safePage, safeSize, totalCount, totalPages);
    }

    // 글 하나 조회 (상세 보기). 없으면 404 에러.
    // viewer: 지금 보고 있는 사람의 아이디 (비로그인이면 null) — 좋아요 여부(likedByMe) 판단에 씀.
    // 조회할 때마다 조회수를 +1 함 (본인 글을 봐도, 새로고침해도 올라가는 단순한 방식 —
    // 실서비스는 IP/세션 기준으로 중복을 걸러내지만 여기선 단순화).
    public PostResult getPost(Long id, String viewer) {
        Post found = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없어요."));

        postRepository.incrementViewCount(id);
        boolean likedByMe = viewer != null && postRepository.hasLiked(id, viewer);

        // 방금 +1한 조회수를 응답에도 반영하려고 다시 조회 (found는 +1 되기 전 값이라서)
        Post post = postRepository.findById(id).orElse(found);
        return PostResult.from(post, likedByMe);
    }

    // 좋아요 토글: 안 눌렀으면 누르고, 이미 눌렀으면 취소. 토글 후의 새 상태를 돌려줌
    public LikeResult toggleLike(Long postId, String username) {
        postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없어요."));

        boolean nowLiked;
        if (postRepository.hasLiked(postId, username)) {
            postRepository.removeLike(postId, username);
            nowLiked = false;
        } else {
            postRepository.addLike(postId, username);
            nowLiked = true;
        }
        return new LikeResult(nowLiked, postRepository.countLikes(postId));
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