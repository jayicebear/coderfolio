package com.example.coderfolio.contexts.board.application.dto;

// ============================================================
//  LikeResult  -  좋아요 토글 후의 새 상태를 돌려주는 결과
//  프론트가 이 값으로 버튼 모양(♥/♡)과 숫자를 즉시 갱신함.
// ============================================================
public class LikeResult {

    private final boolean liked;     // 토글 후 "누른 상태"인지
    private final long likeCount;    // 토글 후 총 좋아요 수

    public LikeResult(boolean liked, long likeCount) {
        this.liked = liked;
        this.likeCount = likeCount;
    }

    public boolean isLiked() { return liked; }
    public long getLikeCount() { return likeCount; }
}
