# Bakery Notification Service

The Bakery Notification Service is a microservice responsible for handling multi-channel communications (Email, SMS) for the Bakery platform.

## Directory Structure

```text
bakery_notification_service/
+---build.gradle.kts
+---gradlew
+---gradlew.bat
\---src
    +---main
    |   +---java
    |   |   \---com
    |   |       \---blubugtech
    |   |           \---bakery_notification_service
    |   |               +---config
    |   |               +---constants
    |   |               +---controller
    |   |               +---dto
    |   |               |   +---device
    |   |               |   +---email
    |   |               |   \---notification
    |   |               +---entity
    |   |               +---enums
    |   |               +---exception
    |   |               +---integration
    |   |               |   +---brevo
    |   |               |   +---email
    |   |               |   \---sms
    |   |               +---kafka
    |   |               |   \---consumer
    |   |               +---mapper
    |   |               +---model
    |   |               +---repository
    |   |               +---service
    |   |               |   +---impl
    |   |               |   \---sender
    |   |               +---strategy
    |   |               \---validation
    |   \---resources
    |       \---db
    |           \---migration
    \---test
        +---java
        |   \---com
        |       \---blubugtech
        |           \---bakery_notification_service
        \---resources
```

## 🔗 Related Links
- [Parent Repository](https://github.com/amankrmj09/Blu_s_Bakery)
- [API Reference](./API_REFERENCE.md)
