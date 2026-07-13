# Bakery Notification Service API Reference

This document outlines the REST APIs exposed by the Bakery Notification Service.

## Base URL
All endpoints are relative to the application's base URL (e.g., `http://localhost:8080`).

---

## 1. Admin API (`/api/admin`)
System administration and monitoring APIs. Requires `ADMIN` or `SYSTEM` roles.

### 1.1 Get System Overview
- **Name:** Get system overview
- **Method:** `GET`
- **Path:** `/api/admin/overview`
- **Headers:** 
  - `X-User-Id` (optional)
- **Request Body:** None
- **Response:** (200 OK)
  ```json
  {
    "system": {
      "applicationName": "bakery-notification-service",
      "serverPort": "8080",
      "timestamp": "2026-07-13T12:00:00",
      "version": "1.0.0",
      "environment": "production"
    },
    "serviceHealth": {
      "email": {
        // Detailed health status of email service
      }
    }
  }
  ```

### 1.2 Test Email Service
- **Name:** Test email service
- **Method:** `POST`
- **Path:** `/api/admin/test/email`
- **Query Parameters:**
  - `testEmail` (optional) - The email address to send the test email to.
- **Headers:** 
  - `X-User-Id` (optional)
- **Request Body:** None
- **Response:** (200 OK or 500 Internal Server Error)
  ```json
  {
    "service": "email",
    "connectivity": true,
    "timestamp": "2026-07-13T12:00:00",
    "health": {
      // Detailed health status of email service
    }
  }
  ```

### 1.3 Get Service Health
- **Name:** Get service health
- **Method:** `GET`
- **Path:** `/api/admin/health`
- **Request Body:** None
- **Response:** (200 OK or 503 Service Unavailable)
  ```json
  {
    "system": {
      "status": "UP",
      "application": "bakery-notification-service",
      "port": "8080",
      "timestamp": "2026-07-13T12:00:00"
    },
    "services": {
      "email": {
        // Detailed health status of email service
      }
    },
    "overallStatus": "HEALTHY"
  }
  ```

---

## 2. Notification API (`/api/notifications`)
Notification management APIs.

### 2.1 Send an Email Notification
- **Name:** Send an email notification
- **Method:** `POST`
- **Path:** `/api/notifications`
- **Headers:** 
  - `X-User-Id` (optional)
- **Request Body:** `SendNotificationRequestDto`
  ```json
  {
    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "recipientEmail": "user@example.com",
    "recipientName": "John Doe",
    "title": "Notification Title",
    "content": "Notification content text",
    "templateId": 1,
    "params": {
      "key": "value"
    }
  }
  ```
- **Response:** (201 Created) `NotificationResponseDto`
  ```json
  {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "recipientEmail": "user@example.com",
    "recipientName": "John Doe",
    "status": "SENT",
    "title": "Notification Title",
    "content": "Notification content text",
    "emailMessageId": "msg-1234",
    "errorMessage": null,
    "createdAt": "2026-07-13T12:00:00",
    "updatedAt": "2026-07-13T12:00:00",
    "sentAt": "2026-07-13T12:00:00"
  }
  ```

### 2.2 Send Bulk Email Notifications
- **Name:** Send bulk email notifications
- **Method:** `POST`
- **Path:** `/api/notifications/bulk`
- **Headers:** 
  - `X-User-Id` (optional)
- **Request Body:** Array of `SendNotificationRequestDto`
  ```json
  [
    {
      "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "recipientEmail": "user@example.com",
      "recipientName": "John Doe",
      "title": "Notification Title",
      "content": "Notification content text",
      "templateId": 1,
      "params": {
        "key": "value"
      }
    }
  ]
  ```
- **Response:** (202 Accepted)
  ```json
  {
    "status": "ACCEPTED",
    "count": 1,
    "message": "Bulk notifications accepted for processing"
  }
  ```

### 2.3 Get Notification by ID
- **Name:** Get notification by ID
- **Method:** `GET`
- **Path:** `/api/notifications/{notificationId}`
- **Path Parameters:**
  - `notificationId` - UUID of the notification.
- **Headers:** 
  - `X-User-Id` (optional)
- **Request Body:** None
- **Response:** (200 OK or 404 Not Found) `NotificationResponseDto`
  ```json
  {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "recipientEmail": "user@example.com",
    "recipientName": "John Doe",
    "status": "SENT",
    "title": "Notification Title",
    "content": "Notification content text",
    "emailMessageId": "msg-1234",
    "errorMessage": null,
    "createdAt": "2026-07-13T12:00:00",
    "updatedAt": "2026-07-13T12:00:00",
    "sentAt": "2026-07-13T12:00:00"
  }
  ```

### 2.4 Get Notifications by User
- **Name:** Get notifications by user
- **Method:** `GET`
- **Path:** `/api/notifications/user/{userId}`
- **Path Parameters:**
  - `userId` - UUID of the user.
- **Headers:** 
  - `X-User-Id` (optional)
- **Request Body:** None
- **Response:** (200 OK) Array of `NotificationResponseDto`
  ```json
  [
    {
      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "recipientEmail": "user@example.com",
      "recipientName": "John Doe",
      "status": "SENT",
      "title": "Notification Title",
      "content": "Notification content text",
      "emailMessageId": "msg-1234",
      "errorMessage": null,
      "createdAt": "2026-07-13T12:00:00",
      "updatedAt": "2026-07-13T12:00:00",
      "sentAt": "2026-07-13T12:00:00"
    }
  ]
  ```

### 2.5 Get Notifications by User with Pagination
- **Name:** Get notifications by user with pagination
- **Method:** `GET`
- **Path:** `/api/notifications/user/{userId}/paginated`
- **Path Parameters:**
  - `userId` - UUID of the user.
- **Query Parameters:**
  - `page` (default `0`)
  - `size` (default `20`)
  - `sortBy` (default `createdAt`)
  - `sortDir` (default `DESC`)
- **Request Body:** None
- **Response:** (200 OK) Page of `NotificationResponseDto`
  ```json
  {
    "content": [
      {
        "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        "recipientEmail": "user@example.com",
        "recipientName": "John Doe",
        "status": "SENT",
        "title": "Notification Title",
        "content": "Notification content text",
        "emailMessageId": "msg-1234",
        "errorMessage": null,
        "createdAt": "2026-07-13T12:00:00",
        "updatedAt": "2026-07-13T12:00:00",
        "sentAt": "2026-07-13T12:00:00"
      }
    ],
    "pageable": {
      "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 20,
      "paged": true,
      "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 1,
    "size": 20,
    "number": 0,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
  }
  ```

### 2.6 Health Check
- **Name:** Health check
- **Method:** `GET`
- **Path:** `/api/notifications/health`
- **Request Body:** None
- **Response:** (200 OK)
  ```json
  {
    "status": "UP",
    "service": "notification-service",
    "timestamp": "2026-07-13T12:00:00",
    "version": "1.0.0"
  }
  ```
