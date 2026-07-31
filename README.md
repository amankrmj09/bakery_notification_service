# 🚀 Notification Service

![Java](https://img.shields.io/badge/Java-25-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.15-brightgreen.svg)
![Database](https://img.shields.io/badge/Database-PostgreSQL-blue.svg)

Welcome to the **Notification Service**, a core component of the Shah's Bakery Microservice Platform. This service is responsible for sending multi-channel notifications (emails, SMS) and alerting users about orders, payments, and account events.

## 📑 Table of Contents
- [Architecture & Design](#-architecture--design)
- [Features](#-features)
- [Folder Structure](#-folder-structure)
- [API Reference](#-api-reference)
- [Configuration](#-configuration)
- [How to Run Locally](#-how-to-run-locally)
- [Testing](#-testing)
- [Dependencies](#-dependencies)
- [Related Links](#-related-links)

## 🏗️ Architecture & Design
Provide a brief overview of the architecture of this service.
- **Data Storage**: PostgreSQL for persisting notification history and configurations. Flyway is used for database migrations.
- **Communication**: 
  - REST API for administrative broadcast notifications and fetching history.
  - Kafka for async event-driven architecture (listening to Order, Payment, and User events).
  - Feign clients for external API integrations, specifically the Brevo API for dispatching transactional emails and SMS.
- **Key Design Patterns**: MVC, Repository Pattern, DTO pattern, and Event-driven consumers.

## ✨ Features
- Multi-channel notification delivery (Email, SMS).
- Campaign and broadcast notification management.
- Integration with Brevo for transactional emails and SMS.
- Extensive Event-Driven architecture listening to Order, Payment, and User Kafka events.

## 📁 Folder Structure
The source code under `src/main/java` is organized as follows:
```text
src/
└── main/
    └── java/.../bakery_notification_service/
        ├── client/     # HTTP clients communicating with external APIs (Brevo).
        ├── config/     # Configurations for Brevo API and Feign error decoders.
        ├── controller/ # Administrative REST endpoints for broadcast notifications.
        ├── dto/        # Data Transfer Objects for email/SMS payloads.
        ├── entity/     # Database entities for tracking sent notifications.
        ├── exception/  # Custom exceptions and global exception handler.
        ├── kafka/      # Event-driven consumers listening to Order, Payment, and User events.
        ├── repository/ # Spring Data JPA interfaces.
        └── service/    # Core logic for rendering templates and sending emails/SMS.
```

## 🌐 API Reference
> [!NOTE]
> For detailed API definitions, request/response bodies, and schemas, please refer to the OpenAPI Reference available via the API Gateway's Swagger UI.

**Key Endpoints:**
- `POST /api/notifications` - Dispatches a new notification to a user.
- `GET /api/notifications/user/{userId}` - Fetches the notification history for a user.
- `GET /api/notifications/{id}` - Retrieves a specific notification by ID.

## ⚙️ Configuration
List required environment variables and configurations.
You can copy `.env.example` to `.env` and fill in the values.

| Variable | Description | Default / Example |
|----------|-------------|-------------------|
| `SERVER_PORT` | Port for the service | `8080` |
| `ACTIVE_PROFILE` | Spring active profile | `dev` |
| `BREVO_API_KEY` | API Key for Brevo integration | `your-brevo-api-key` |
| `CONFIG_SERVER_URL` | URL for Spring Cloud Config Server | `http://localhost:8888` |
| `EUREKA_URL` | Eureka server URL | `http://localhost:8761/eureka/` |
| `KAFKA_BOOTSTRAP_SERVERS`| Kafka brokers connection string | `localhost:9092` |
| `NOTIFICATION_DB_URL` | Database connection URL | `jdbc:postgresql://localhost:5432/notification_db` |
| `NOTIFICATION_DB_USER` | Database username | `postgres` |
| `NOTIFICATION_DB_PASSWORD` | Database password | `postgres` |
| `NOTIFICATION_FROM_EMAIL`| Default sender email | `noreply@shahs-bakery.com` |
| `EMAIL_ENABLED` | Flag to enable/disable email sending | `true` |

*(Note: Additional variables for specific email templates, like `TEMPLATE_AUTH_WELCOME`, `TEMPLATE_ORDER_CONFIRMATION`, and limits are also available in `.env.example`)*

## 🚀 How to Run Locally

### Prerequisites
- JDK 25+
- Gradle
- PostgreSQL Database
- Kafka Message Broker

### Steps
1. **Clone the repository:**
   ```bash
   git clone https://github.com/amankrmj01/bakery_notification_service.git
   cd bakery_notification_service
   ```

2. **Configure Environment:**
   Set up your `.env` file based on `.env.example`. Make sure backing services (like PostgreSQL and Kafka) are running.

3. **Run the application:**
   ```bash
   ./gradlew bootRun
   ```

## 🧪 Testing
To run the test suite:
```bash
./gradlew test
```

## 🛠️ Dependencies
- **Framework:** Spring Boot 3.5.15
- **Database:** PostgreSQL
- **Key Modules:** Spring Web, Spring Data JPA, Eureka Client, Spring Kafka, Spring Cloud OpenFeign, Flyway, MapStruct, Brevo API

## 🔗 Related Links
- [Main Platform README](../README.md)