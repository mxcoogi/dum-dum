# Store API

Base URL: `/api/stores`

## 공통

**인증 헤더** (인증 필요 항목)
```
Authorization: Bearer {accessToken}
```

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

## 가게 등록

**POST** `/api/stores`

인증 필요

**Request Body**
```json
{
  "name": "홍길동 베이커리",
  "description": "매일 신선한 빵",
  "phoneNumber": "010-1234-5678",
  "address": "서울시 마포구 어딘가 123",
  "latitude": 37.5665,
  "longitude": 126.9780,
  "businessRegistrationNumber": "123-45-67890"
}
```

| 필드 | 타입 | 필수 |
|------|------|------|
| name | String | O |
| description | String | X |
| phoneNumber | String | X |
| address | String | O |
| latitude | Double | O |
| longitude | Double | O |
| businessRegistrationNumber | String | O |

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공",
  "data": {
    "storeId": 1,
    "name": "홍길동 베이커리",
    "verificationStatus": "PENDING"
  }
}
```

> 등록 직후 인증 상태는 `PENDING`. 어드민 승인 후 상품 등록 가능.

**Error**
| 코드 | 상황 |
|------|------|
| DUPLICATE_BUSINESS_NUMBER | 이미 등록된 사업자번호 |

---

## 내 가게 목록

**GET** `/api/stores/my`

인증 필요

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공",
  "data": [
    {
      "storeId": 1,
      "name": "홍길동 베이커리",
      "address": "서울시 마포구 어딘가 123",
      "verificationStatus": "VERIFIED"
    }
  ]
}
```

---

## 가게 상세 조회

**GET** `/api/stores/{storeId}`

인증 불필요

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공",
  "data": {
    "storeId": 1,
    "name": "홍길동 베이커리",
    "description": "매일 신선한 빵",
    "phoneNumber": "010-1234-5678",
    "address": "서울시 마포구 어딘가 123",
    "latitude": 37.5665,
    "longitude": 126.9780,
    "profileImageUrl": "https://...",
    "verificationStatus": "VERIFIED"
  }
}
```

**Error**
| 코드 | 상황 |
|------|------|
| STORE_NOT_FOUND | 존재하지 않는 가게 |

---

## 가게 정보 수정

**PUT** `/api/stores/{storeId}`

인증 필요 (본인 가게만)

**Request Body**
```json
{
  "name": "홍길동 베이커리 2호점",
  "description": "리뉴얼 오픈",
  "phoneNumber": "010-9999-8888",
  "profileImageUrl": "https://..."
}
```

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공"
}
```

**Error**
| 코드 | 상황 |
|------|------|
| STORE_NOT_FOUND | 존재하지 않는 가게 |
| STORE_ACCESS_DENIED | 본인 가게 아님 |

---

## 가게 인증 (어드민)

**POST** `/api/admin/stores/{storeId}/verify`

인증 필요 (`ROLE_ADMIN`)

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공"
}
```

**Error**
| 코드 | 상황 |
|------|------|
| STORE_NOT_FOUND | 존재하지 않는 가게 |
| FORBIDDEN | 어드민 아님 |

---

## 가게 인증 거절 (어드민)

**POST** `/api/admin/stores/{storeId}/reject`

인증 필요 (`ROLE_ADMIN`)

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공"
}
```

**Error**
| 코드 | 상황 |
|------|------|
| STORE_NOT_FOUND | 존재하지 않는 가게 |
| FORBIDDEN | 어드민 아님 |
