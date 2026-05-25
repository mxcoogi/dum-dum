# Auth API

Base URL: `/api/auth`

> `clientType`: `WEB` / `MOBILE` — 토큰 발급 요청 시 필수
> Refresh Token 만료: WEB 7일, MOBILE 30일

## 공통

**공통 에러 응답**

| 코드 | HTTP | 상황 |
|------|------|------|
| UNAUTHORIZED | 401 | 토큰 없음 또는 만료 |
| FORBIDDEN | 403 | 권한 없음 |
| INVALID_INPUT | 400 | 입력값 검증 실패 |
| INTERNAL_SERVER_ERROR | 500 | 서버 오류 |

```json
{ "code": "UNAUTHORIZED", "message": "로그인이 필요합니다" }
```

---

## 회원가입

**POST** `/api/auth/signup`

인증 불필요

**Request Body**
```json
{
  "nickname": "홍길동",
  "email": "user@example.com",
  "password": "password123",
  "clientType": "WEB"
}
```

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| nickname | String | O | 2~20자 |
| email | String | O | 이메일 형식 |
| password | String | O | 8~20자 |
| clientType | Enum | O | WEB \| MOBILE |

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공",
  "data": {
    "accessToken": "eyJhbGci...",
    "refreshToken": "eyJhbGci..."
  }
}
```

**Error**

| 코드 | 상황 |
|------|------|
| DUPLICATE_EMAIL | 이미 사용 중인 이메일 |
| INVALID_INPUT | 입력값 검증 실패 |

---

## 로컬 로그인

**POST** `/api/auth/login`

인증 불필요 / WEB·MOBILE 공통

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "clientType": "MOBILE"
}
```

| 필드 | 타입 | 필수 |
|------|------|------|
| email | String | O |
| password | String | O |
| clientType | Enum | O |

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공",
  "data": {
    "accessToken": "eyJhbGci...",
    "refreshToken": "eyJhbGci..."
  }
}
```

**Error**

| 코드 | 상황 |
|------|------|
| USER_NOT_FOUND | 존재하지 않는 이메일 |
| INVALID_PASSWORD | 비밀번호 불일치 |

---

## 소셜 로그인 (웹 — Spring Security OAuth2)

**GET** `/oauth2/authorization/{provider}`

인증 불필요 / 브라우저에서 URL로 이동 → OAuth2 flow → 리다이렉트

| provider | 값 |
|----------|----|
| 카카오 | `kakao` |
| 네이버 | `naver` |
| 구글 | `google` |
| 애플 | `apple` |

**Redirect** `{OAUTH2_REDIRECT_URI}?accessToken=...&refreshToken=...`

> 웹 OAuth2 flow는 항상 `clientType: WEB`으로 처리

**Redirect (실패)** `{OAUTH2_REDIRECT_URI}?error=oauth2_failed`

---

## 소셜 로그인 (모바일 — 네이티브 SDK)

**POST** `/api/auth/oauth2/{provider}`

인증 불필요

| provider | 값 |
|----------|----|
| 카카오 | `kakao` |
| 네이버 | `naver` |
| 구글 | `google` |
| 애플 | `apple` |

**Request Body**
```json
{
  "providerToken": "카카오SDK에서_발급받은_액세스토큰",
  "clientType": "MOBILE"
}
```

> Apple은 `providerToken`에 ID Token (JWT) 전달

| 필드 | 타입 | 필수 |
|------|------|------|
| providerToken | String | O |
| clientType | Enum | O |

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공",
  "data": {
    "accessToken": "eyJhbGci...",
    "refreshToken": "eyJhbGci..."
  }
}
```

**Error**

| 코드 | 상황 |
|------|------|
| INVALID_TOKEN | 유효하지 않은 provider token |
| INVALID_INPUT | 지원하지 않는 provider |

---

## 토큰 재발급

**POST** `/api/auth/refresh`

인증 불필요 / WEB·MOBILE 공통

**Request Body**
```json
{
  "refreshToken": "eyJhbGci...",
  "clientType": "WEB"
}
```

| 필드 | 타입 | 필수 |
|------|------|------|
| refreshToken | String | O |
| clientType | Enum | O |

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공",
  "data": {
    "accessToken": "eyJhbGci...",
    "refreshToken": "eyJhbGci..."
  }
}
```

> refresh token rotate — 재발급 시 기존 토큰 즉시 무효화

**Error**

| 코드 | 상황 |
|------|------|
| INVALID_TOKEN | 유효하지 않은 토큰 |
| EXPIRED_TOKEN | 만료된 토큰 |

---

## 로그아웃

**POST** `/api/auth/logout`

인증 불필요 / WEB·MOBILE 공통

**Request Body**
```json
{
  "refreshToken": "eyJhbGci...",
  "clientType": "WEB"
}
```

| 필드 | 타입 | 필수 |
|------|------|------|
| refreshToken | String | O |
| clientType | Enum | O |

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공"
}
```
