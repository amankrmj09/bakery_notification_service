# Bakery Notification Service API Reference

This document provides complete documentation for the REST API endpoints provided by the **Bakery Notification Service**.

---

## Table of Contents
- [Overview](#overview)
- [Authentication & Security](#authentication--security)
- [System & Health Actuator Endpoints](#system--health-actuator-endpoints)
- [Notification Management APIs](#notification-management-apis)
  - [1. Send Notification](#1-send-notification)
  - [2. Send Bulk Notifications](#2-send-bulk-notifications)
  - [3. Get Notification by ID](#3-get-notification-by-id)
  - [4. Get Notifications by User](#4-get-notifications-by-user)
  - [5. Get Notifications by User (Paginated)](#5-get-notifications-by-user-paginated)

---

## Overview

The Notification Service handles multi-channel communication (Email, SMS) for Shah's Bakery platform, providing endpoints for administrative dispatch and notification retrieval by user or notification ID.

- **Base URL:** `/api/notifications`
- **Content-Type:** `application/json`

---

## Authentication & Security

Endpoints are secured with Spring Security annotations. The API expects authorization roles:
- `ADMIN` - Full access to all endpoints
- `SYSTEM` - Internal service-to-service communication
- `MARKETING` - Access to dispatch individual or bulk marketing notifications
- `USER` - Access to view owned notifications

Headers:
- `X-User-Id` (Optional): Requesting user's ID passed by the API Gateway.

---

## System & Health Actuator Endpoints

Standard Spring Boot Actuator endpoints for system health and monitoring.

### Health Check
- **Method:** `GET`
- **Path:** `/actuator/health`
- **Access:** Public
- **Response Code:** `200 OK`
- **Response Body:**
```json
{
  "status": "UP"
}
```

### Service Info
- **Method:** `GET`
- **Path:** `/actuator/info`
- **Access:** Public
- **Response Code:** `200 OK`

### Metrics (Prometheus)
- **Method:** `GET`
- **Path:** `/actuator/prometheus`
- **Access:** Public / Monitoring
- **Response Code:** `200 OK`

---

## Notification Management APIs

### 1. Send Notification

Dispatches a single multi-channel notification (Email/SMS) to a recipient.

- **Method:** `POST`
- **Path:** `/api/notifications`
- **Access:** `ADMIN`, `SYSTEM`, `MARKETING`
- **Headers:**
  - `X-User-Id` (String, optional): Requesting user ID.

#### Request Body (JSON)
[`SendNotificationRequest`](./src/main/java/com/blubugtech/bakery_notification_service/dto/notification/SendNotificationRequest.java)
```json
{
  "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "recipientEmail": "user@example.com",
  "recipientName": "John Doe",
  "title": "Welcome to Shah's Bakery",
  "content": "Thank you for creating an account with us!",
  "recipientPhone": "+1234567890",
  "smsContent": "Welcome to Shah's Bakery!",
  "smsTag": "WELCOME",
  "channels": ["EMAIL", "SMS"],
  "templateId": 1,
  "params": {
    "customerName": "John"
  }
}
```

#### Response Body (JSON)
[`NotificationResponse`](./src/main/java/com/blubugtech/bakery_notification_service/dto/notification/NotificationResponse.java)
```json
{
  "id": "c1f7b889-1029-4d2a-89a1-5aefd1234567",
  "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "recipientEmail": "user@example.com",
  "recipientName": "John Doe",
  "status": "SENT",
  "title": "Welcome to Shah's Bakery",
  "content": "Thank you for creating an account with us!",
  "emailMessageId": "<202607131200.12345@brevo.com>",
  "errorMessage": null,
  "createdAt": "2026-08-05T12:00:00",
  "updatedAt": "2026-08-05T12:00:01",
  "sentAt": "2026-08-05T12:00:01"
}
```

#### Error Responses
- **400 Bad Request:** Validation failed (e.g., missing email, invalid format).
  ```json
  {
    "code": "CONSTRAINT_VIOLATION",
    "message": "Constraint violation in request data",
    "validationErrors": {
      "recipientEmail": "Email is required"
    },
    "timestamp": "2026-08-05T12:00:00",
    "path": "/api/notifications"
  }
  ```
- **403 Forbidden:** Insufficient permissions.
  ```json
  {
    "code": "ACCESS_DENIED",
    "message": "Access denied - insufficient permissions",
    "timestamp": "2026-08-05T12:00:00",
    "path": "/api/notifications"
  }
  ```

---

### 2. Send Bulk Notifications

Enqueues multiple notification dispatch requests in bulk.

- **Method:** `POST`
- **Path:** `/api/notifications/bulk`
- **Access:** `ADMIN`, `SYSTEM`, `MARKETING`
- **Headers:**
  - `X-User-Id` (String, optional): Requesting user ID.

#### Request Body (JSON)
```json
[
  {
    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "recipientEmail": "user1@example.com",
    "recipientName": "John Doe",
    "title": "Weekly Offer",
    "content": "Special discount on bakery items!",
    "recipientPhone": "+1234567890",
    "smsContent": "Special discount on bakery items!",
    "smsTag": "PROMO",
    "channels": ["EMAIL", "SMS"],
    "templateId": 2,
    "params": {
      "discountPercent": 10
    }
  }
]
```

#### Response Body (JSON)
```json
{
  "message": "Bulk notifications accepted for processing. Count: 1"
}
```

#### Error Responses
- **400 Bad Request:** Malformed JSON or invalid data.
  ```json
  {
    "code": "MALFORMED_REQUEST",
    "message": "Malformed JSON request",
    "timestamp": "2026-08-05T12:00:00",
    "path": "/api/notifications/bulk"
  }
  ```

---

### 3. Get Notification by ID

Retrieves details of a specific notification by its unique UUID.

- **Method:** `GET`
- **Path:** `/api/notifications/{notificationId}`
- **Access:** `ADMIN`, `SYSTEM`, `USER`
- **Path Parameters:**
  - `notificationId` (UUID, required): The ID of the notification.

#### Response Body (JSON)
[`NotificationResponse`](./src/main/java/com/blubugtech/bakery_notification_service/dto/notification/NotificationResponse.java)
```json
{
  "id": "c1f7b889-1029-4d2a-89a1-5aefd1234567",
  "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "recipientEmail": "user@example.com",
  "recipientName": "John Doe",
  "status": "SENT",
  "title": "Welcome to Shah's Bakery",
  "content": "Thank you for creating an account with us!",
  "emailMessageId": "<202607131200.12345@brevo.com>",
  "errorMessage": null,
  "createdAt": "2026-08-05T12:00:00",
  "updatedAt": "2026-08-05T12:00:01",
  "sentAt": "2026-08-05T12:00:01"
}
```

#### Error Responses
- **400 Bad Request:** Invalid notification ID format.
  ```json
  {
    "code": "INVALID_PARAMETER_TYPE",
    "message": "Invalid value 'abc' for parameter 'notificationId'. Expected type: UUID",
    "timestamp": "2026-08-05T12:00:00",
    "path": "/api/notifications/abc"
  }
  ```

---

### 4. Get Notifications by User

Retrieves paginated notifications for a specific user.

- **Method:** `GET`
- **Path:** `/api/notifications/user/{userId}`
- **Access:** `ADMIN`, `SYSTEM`, or owner (`#userId == authentication.name`)
- **Path Parameters:**
  - `userId` (UUID, required): Target user's UUID.
- **Query Parameters:**
  - `page` (Integer, default `0`): Page index.
  - `size` (Integer, default `20`): Page size.
  - `sortBy` (String, default `createdAt`): Property name for sorting.
  - `sortDir` (String, default `DESC`): Sort direction (`ASC` or `DESC`).

#### Response Code: `200 OK`
#### Response Body:
[`NotificationResponse`](./src/main/java/com/blubugtech/bakery_notification_service/dto/notification/NotificationResponse.java)
```json
{
  "content": [
    {
      "id": "c1f7b889-1029-4d2a-89a1-5aefd1234567",
      "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "recipientEmail": "user@example.com",
      "recipientName": "John Doe",
      "status": "SENT",
      "title": "Welcome to Shah's Bakery",
      "content": "Thank you for creating an account with us!",
      "emailMessageId": "<202607131200.12345@brevo.com>",
      "errorMessage": null,
      "createdAt": "2026-08-05T12:00:00",
      "updatedAt": "2026-08-05T12:00:01",
      "sentAt": "2026-08-05T12:00:01"
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

### 5. Get Notifications by User (Paginated)

Alternative endpoint for retrieving paginated user notifications.

- **Method:** `GET`
- **Path:** `/api/notifications/user/{userId}/paginated`
- **Access:** `ADMIN`, `SYSTEM`, or owner (`#userId == authentication.name`)
- **Path Parameters:**
  - `userId` (UUID, required): Target user's UUID.
- **Query Parameters:**
  - `page` (Integer, default `0`): Page index.
  - `size` (Integer, default `20`): Page size.
  - `sortBy` (String, default `createdAt`): Property name for sorting.
  - `sortDir` (String, default `DESC`): Sort direction (`ASC` or `DESC`).

#### Response Code: `200 OK`
#### Response Body:
[`NotificationResponse`](./src/main/java/com/blubugtech/bakery_notification_service/dto/notification/NotificationResponse.java)
```json
{
  "content": [
    {
      "id": "c1f7b889-1029-4d2a-89a1-5aefd1234567",
      "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "recipientEmail": "user@example.com",
      "recipientName": "John Doe",
      "status": "SENT",
      "title": "Welcome to Shah's Bakery",
      "content": "Thank you for creating an account with us!",
      "emailMessageId": "<202607131200.12345@brevo.com>",
      "errorMessage": null,
      "createdAt": "2026-08-05T12:00:00",
      "updatedAt": "2026-08-05T12:00:01",
      "sentAt": "2026-08-05T12:00:01"
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 1,
    "totalPages": 1
  }
}
```
