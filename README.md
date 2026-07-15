# 🧁 Notification Service

![Java](https://img.shields.io/badge/Java-21%2B-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)

Welcome to the **Notification Service**, a core component of the Shah's Bakery Microservice Platform.

## 📑 Table of Contents
- [Features](#-features)
- [Folder Structure](#-folder-structure)
- [Dependencies](#-dependencies)
- [Endpoints](#-endpoints)
- [How to Run](#-how-to-run)
- [Related Links](#-related-links)

## ✨ Features
- Multi-channel notification delivery (Email, SMS).
- Campaign and broadcast notification management.
- Integration with Brevo for transactional emails and SMS.
- Extensive Event-Driven architecture listening to Order, Payment, and User Kafka events.

## 📁 Folder Structure
The main `src/main/java` directory is organized as follows:
```text
src/
└── main/
    └── java/.../bakery_notification_service/
        ├── client/     # HTTP clients communicating with external APIs (Brevo).
        ├── config/     # Configurations for Brevo API and Feign error decoders.
        ├── controller/ # Administrative REST endpoints for broadcast notifications.
        ├── dto/        # Data Transfer Objects for email/SMS payloads.
        ├── entity/     # Database entities for tracking sent notifications.
        ├── exception/  # Custom exceptions.
        ├── kafka/      # Event-driven consumers listening to Order, Payment, and User events.
        ├── repository/ # Spring Data JPA interfaces.
        └── service/    # Core logic for rendering templates and sending emails/SMS.
```

## 🛠️ Dependencies
- **Framework:** Spring Boot
- **Database:** PostgreSQL
- **Key Modules:** Eureka Client, Brevo API

## 🌐 Endpoints
> [!NOTE]
> For complete and detailed API definitions, please refer to the OpenAPI Reference available via the API Gateway's Swagger UI.

- `POST /api/notifications` - Dispatches a new notification to a user.
- `GET /api/notifications/user/{userId}` - Fetches the notification history for a user.
- `GET /api/notifications/{id}` - Retrieves a specific notification by ID.

## 🚀 How to Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/amankrmj01/bakery_notification_service.git
   cd bakery_notification_service
   ```

2. **Configure Environment:**
   Ensure your AWS/Twilio API keys and DB credentials are set in the configuration.

3. **Run the application:**
   ```bash
   ./gradlew bootRun
   ```

## 🔗 Related Links
- [Main Platform README](../README.md)