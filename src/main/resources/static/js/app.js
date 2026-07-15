// ============================================================
//  app.js  -  모든 페이지가 공유하는 공용 헬퍼
//  이제 서버는 JSON만 주므로, 화면은 이 JS들이 fetch()로 데이터를
//  받아와서 직접 그림 (예전에 Thymeleaf가 서버에서 하던 일).
// ============================================================

// ---- API 호출 공통 함수 ----
// 사용법: await api('/api/posts')                          → GET
//        await api('/api/auth/login', { method:'POST', body:{...} })
// 실패(4xx/5xx)하면 서버가 내려준 { message } 를 담아 Error를 던짐.
async function api(path, options = {}) {
    const res = await fetch(path, {
        method: options.method || 'GET',
        headers: options.body ? { 'Content-Type': 'application/json' } : {},
        body: options.body ? JSON.stringify(options.body) : undefined,
    });

    if (!res.ok) {
        let message = '요청에 실패했어요.';
        try {
            const data = await res.json();
            if (data && data.message) message = data.message;
        } catch (ignore) { /* 본문이 JSON이 아니면 기본 메시지 사용 */ }
        const err = new Error(message);
        err.status = res.status;   // 호출한 쪽에서 401 등을 구분할 수 있게
        throw err;
    }

    // 204 No Content 처럼 본문이 없는 성공 응답 처리
    if (res.status === 204) return null;
    const type = res.headers.get('content-type') || '';
    return type.includes('json') ? res.json() : null;
}

// ---- 로그인 확인 ----
// 로그인 필수 페이지 맨 위에서 호출. 안 돼 있으면 로그인 화면으로 보냄.
async function requireLogin() {
    try {
        return await api('/api/auth/me');   // { username: "..." }
    } catch (e) {
        location.href = '/login.html';
        throw e;   // 이후 코드 실행 중단
    }
}

// 로그인 여부만 조용히 확인 (안 돼 있어도 튕기지 않음, null 리턴)
async function currentUser() {
    try {
        return await api('/api/auth/me');
    } catch (e) {
        return null;
    }
}

async function logout() {
    await api('/api/auth/logout', { method: 'POST' });
    location.href = '/login.html';
}

// ---- 화면 그리기 헬퍼 ----

// 사용자 입력을 HTML에 넣기 전에 특수문자를 무해하게 바꿈 (XSS 방지)
// Thymeleaf의 th:text가 자동으로 해주던 일을 이제 우리가 직접 함.
function esc(s) {
    if (s == null) return '';
    return String(s)
        .replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;').replaceAll("'", '&#39;');
}

// "2026-07-13T14:30:00" → "2026-07-13 14:30"
function fmtDate(iso) {
    if (!iso) return '';
    return iso.replace('T', ' ').slice(0, 16);
}

// 값이 비어있지 않을 때만 html 조각을 만들어주는 헬퍼
function ifValue(v, html) {
    return (v != null && String(v).trim() !== '') ? html : '';
}
