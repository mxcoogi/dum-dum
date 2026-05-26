# Product API

Base URL: `/api`

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

## 근처 마감 상품 목록

**GET** `/api/products?lat={위도}&lng={경도}&radius={km}`

인증 불필요

**Query Parameters**
| 파라미터 | 타입 | 필수 | 기본값 |
|----------|------|------|--------|
| lat | Double | O | - |
| lng | Double | O | - |
| radius | Double | X | 2.0 |

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공",
  "data": [
    {
      "productId": 1,
      "storeName": "홍길동 베이커리",
      "name": "크루아상 3개 묶음",
      "originalPrice": 9000,
      "discountedPrice": 4500,
      "remainingQuantity": 3,
      "pickupDeadline": "2024-01-15T21:00:00",
      "thumbnailImageUrl": "https://...",
      "distance": 0.8
    }
  ]
}
```

---

## 상품 상세 조회

**GET** `/api/products/{productId}`

인증 불필요

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공",
  "data": {
    "productId": 1,
    "storeId": 1,
    "storeName": "홍길동 베이커리",
    "name": "크루아상 3개 묶음",
    "description": "오늘 구운 크루아상입니다",
    "originalPrice": 9000,
    "discountedPrice": 4500,
    "totalQuantity": 5,
    "remainingQuantity": 3,
    "pickupDeadline": "2024-01-15T21:00:00",
    "status": "AVAILABLE",
    "images": [
      { "imageUrl": "https://...", "displayOrder": 0 }
    ]
  }
}
```

**Error**
| 코드 | 상황 |
|------|------|
| PRODUCT_NOT_FOUND | 존재하지 않는 상품 |

---

## 상품 등록

**POST** `/api/stores/{storeId}/products`

인증 필요 (본인 가게 + VERIFIED 상태)

**Request Body**
```json
{
  "name": "크루아상 3개 묶음",
  "description": "오늘 구운 크루아상입니다",
  "originalPrice": 9000,
  "discountedPrice": 4500,
  "quantity": 5,
  "pickupDeadline": "2024-01-15T21:00:00"
}
```

| 필드 | 타입 | 필수 |
|------|------|------|
| name | String | O |
| description | String | X |
| originalPrice | Integer | O |
| discountedPrice | Integer | O |
| quantity | Integer | O |
| pickupDeadline | LocalDateTime | O |

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공",
  "data": {
    "productId": 1
  }
}
```

**Error**
| 코드 | 상황 |
|------|------|
| STORE_NOT_FOUND | 존재하지 않는 가게 |
| STORE_ACCESS_DENIED | 본인 가게 아님 |
| STORE_NOT_VERIFIED | 미인증 가게 |

---

## 상품 이미지 업로드

**POST** `/api/products/{productId}/images`

인증 필요 (본인 가게)

**Request** `multipart/form-data`
| 필드 | 타입 | 설명 |
|------|------|------|
| images | File[] | 이미지 파일 (최대 5장) |

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
| PRODUCT_NOT_FOUND | 존재하지 않는 상품 |
| STORE_ACCESS_DENIED | 본인 가게 아님 |

---

## 상품 취소

**DELETE** `/api/products/{productId}`

인증 필요 (본인 가게)

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
| PRODUCT_NOT_FOUND | 존재하지 않는 상품 |
| STORE_ACCESS_DENIED | 본인 가게 아님 |
| PRODUCT_UNAVAILABLE | EXPIRED·CANCELLED 상태 취소 불가 |
