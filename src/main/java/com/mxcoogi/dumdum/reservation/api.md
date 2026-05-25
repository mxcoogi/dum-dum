# Reservation API

Base URL: `/api/reservations`

## 공통

**인증 헤더** (전체 인증 필요)
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

## 예약

**POST** `/api/reservations`

**Request Body**
```json
{
  "productId": 1,
  "quantity": 2
}
```

| 필드 | 타입 | 필수 |
|------|------|------|
| productId | Long | O |
| quantity | Integer | O |

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공",
  "data": {
    "reservationId": 1,
    "productName": "크루아상 3개 묶음",
    "quantity": 2,
    "pickupDeadline": "2024-01-15T21:00:00",
    "status": "PENDING"
  }
}
```

**Error**
| 코드 | 상황 |
|------|------|
| PRODUCT_NOT_FOUND | 존재하지 않는 상품 |
| PRODUCT_UNAVAILABLE | 예약 불가 상품 (SOLD_OUT, EXPIRED, CANCELLED) |
| OUT_OF_STOCK | 재고 부족 |
| DUPLICATE_RESERVATION | 동일 상품 중복 예약 |

---

## 내 예약 목록

**GET** `/api/reservations`

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공",
  "data": [
    {
      "reservationId": 1,
      "storeName": "홍길동 베이커리",
      "productName": "크루아상 3개 묶음",
      "quantity": 2,
      "pickupDeadline": "2024-01-15T21:00:00",
      "status": "PENDING"
    }
  ]
}
```

---

## 예약 상세

**GET** `/api/reservations/{reservationId}`

**Response** `200`
```json
{
  "code": "SUCCESS",
  "message": "성공",
  "data": {
    "reservationId": 1,
    "storeId": 1,
    "storeName": "홍길동 베이커리",
    "storeAddress": "서울시 마포구 어딘가 123",
    "productId": 1,
    "productName": "크루아상 3개 묶음",
    "quantity": 2,
    "pickupDeadline": "2024-01-15T21:00:00",
    "status": "PENDING",
    "createdAt": "2024-01-15T18:30:00"
  }
}
```

**Error**
| 코드 | 상황 |
|------|------|
| RESERVATION_NOT_FOUND | 존재하지 않는 예약 |
| RESERVATION_ACCESS_DENIED | 본인 예약 아님 |

---

## 예약 취소

**DELETE** `/api/reservations/{reservationId}`

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
| RESERVATION_NOT_FOUND | 존재하지 않는 예약 |
| RESERVATION_ACCESS_DENIED | 본인 예약 아님 |
| CANNOT_CANCEL | 취소 불가 상태 (COMPLETED, NO_SHOW) |

---

## 방문 완료 처리 (가게 사장님)

**POST** `/api/reservations/{reservationId}/complete`

> QR 또는 예약번호 확인 후 사장님이 호출

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
| RESERVATION_NOT_FOUND | 존재하지 않는 예약 |
| STORE_ACCESS_DENIED | 해당 가게 사장님 아님 |
| CANNOT_CANCEL | 이미 완료 또는 취소된 예약 |

---

## 노쇼 자동 처리 (스케줄러)

스케줄러가 주기적으로 실행:
- `pickupDeadline` 초과된 `PENDING` 예약 → `NO_SHOW` 자동 전환
- 해당 유저 `noShowCount` +1
- 취소된 수량만큼 `Product.remainingQuantity` 복구
