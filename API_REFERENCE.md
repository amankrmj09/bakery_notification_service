# Bakery Notification Service API Reference

This document outlines the REST APIs exposed by the Bakery Notification Service.

---

## 1. Admin API
System administration and monitoring APIs. Requires `ADMIN` or `SYSTEM` roles.
**Base Path:** `/api/admin`

### 1.1 Get System Overview
- **Method:** `GET`
- **Path:** `/api/admin/overview`
- **Type of API:** `Admin`
- **Request Body:** None
- **Response Body:** `200 OK`
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
- **Method:** `POST`
- **Path:** `/api/admin/test/email`
- **Type of API:** `Admin`
- **Request Body:** None
- **Response Body:** `200 OK`
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
- **Method:** `GET`
- **Path:** `/api/admin/health`
- **Type of API:** `Admin`
- **Request Body:** None
- **Response Body:** `200 OK`
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

## 2. Notification API
Notification management APIs.
**Base Path:** `/api/notifications`

### 2.1 Send an Email Notification
- **Method:** `POST`
- **Path:** `/api/notifications`
- **Type of API:** `Admin`
- **Request Body:**
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
- **Response Body:** `201 Created`
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
- **Method:** `POST`
- **Path:** `/api/notifications/bulk`
- **Type of API:** `Admin`
- **Request Body:**
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
- **Response Body:** `202 Accepted`
  ```json
  {
    "status": "ACCEPTED",
    "count": 1,
    "message": "Bulk notifications accepted for processing"
  }
  ```

### 2.3 Get Notification by ID
- **Method:** `GET`
- **Path:** `/api/notifications/{notificationId}`
- **Type of API:** `User`
- **Request Body:** None
- **Response Body:** `200 OK`
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
- **Method:** `GET`
- **Path:** `/api/notifications/user/{userId}`
- **Type of API:** `User`
- **Request Body:** None
- **Response Body:** `200 OK`
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
- **Method:** `GET`
- **Path:** `/api/notifications/user/{userId}/paginated`
- **Type of API:** `User`
- **Request Body:** None
- **Response Body:** `200 OK`
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
- **Method:** `GET`
- **Path:** `/api/notifications/health`
- **Type of API:** `Public`
- **Request Body:** None
- **Response Body:** `200 OK`
  ```json
  {
    "status": "UP",
    "service": "notification-service",
    "timestamp": "2026-07-13T12:00:00",
    "version": "1.0.0"
  }
  ```
