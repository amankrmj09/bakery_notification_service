# bakery_notification_service API Report

## NotificationController

### `POST` `/api/notifications`
- **API Name:** sendNotification
- **Type:** REST / Synchronous
- **Request Headers:**
  - `X-User-Id` (String, optional)
  - `X-User-Role` (String, optional)

**Request:**
```json
{
  "userId": "UUID - NULL for broadcast",
  "recipientEmail": "String - Email format",
  "recipientPhone": "String - Phone format",
  "recipientName": "String",
  "type": "String - Required (e.g., EMAIL, SMS, PUSH, IN_APP)",
  "priority": "String - Default NORMAL (LOW, NORMAL, HIGH, URGENT)",
  "templateId": "UUID",
  "campaignId": "UUID",
  "title": "String - Required",
  "content": "String - Required",
  "htmlContent": "String",
  "subject": "String",
  "pushToken": "String",
  "platform": "String - iOS, ANDROID, WEB",
  "scheduledAt": "DateTime",
  "expiresAt": "DateTime",
  "maxRetryCount": "Integer - Default 3",
  "relatedEntityType": "String - ORDER, CART, USER, PRODUCT",
  "relatedEntityId": "UUID",
  "source": "String",
  "triggeredBy": "String",
  "templateVariables": {},
  "pushData": {},
  "metadata": {},
  "trackingParams": {}
}
```

**Response:**
```json
{
  "id": "UUID",
  "userId": "UUID",
  "recipientEmail": "String",
  "recipientPhone": "String",
  "recipientName": "String",
  "type": "String",
  "status": "String",
  "priority": "String",
  "templateId": "UUID",
  "campaignId": "UUID",
  "title": "String",
  "content": "String",
  "htmlContent": "String",
  "subject": "String",
  "pushToken": "String",
  "snsEndpointArn": "String",
  "snsMessageId": "String",
  "platform": "String",
  "twilioMessageSid": "String",
  "emailMessageId": "String",
  "bounceCount": "Integer",
  "retryCount": "Integer",
  "maxRetryCount": "Integer",
  "scheduledAt": "DateTime",
  "sentAt": "DateTime",
  "deliveredAt": "DateTime",
  "failedAt": "DateTime",
  "openedAt": "DateTime",
  "clickedAt": "DateTime",
  "errorMessage": "String",
  "errorCode": "String",
  "lastErrorAt": "DateTime",
  "relatedEntityType": "String",
  "relatedEntityId": "UUID",
  "source": "String",
  "triggeredBy": "String",
  "createdAt": "DateTime",
  "updatedAt": "DateTime",
  "expiresAt": "DateTime",
  "metadata": {},
  "trackingData": {},
  "canRetry": "Boolean",
  "isExpired": "Boolean",
  "isPending": "Boolean",
  "isScheduled": "Boolean",
  "shouldSendNow": "Boolean"
}
```

---

### `POST` `/api/notifications/bulk`
- **API Name:** sendBulkNotifications
- **Type:** REST / Synchronous
- **Request Headers:** `X-User-Id` (optional)

**Request:**
```json
[
  {
    "userId": "UUID",
    "type": "String",
    "title": "String",
    "content": "String",
    "..." : "..."
  }
]
```
*(Array of SendNotificationRequest objects)*

**Response:**
```json
{
  "status": "String - ACCEPTED",
  "count": "Integer",
  "message": "String"
}
```

---

### `GET` `/api/notifications/{notificationId}`
- **API Name:** getNotificationById
- **Type:** REST / Synchronous
- **Path Variable:** `notificationId` (UUID)

**Request:**
None

**Response:**
*(Same as `sendNotification` NotificationResponse)*

---

### `GET` `/api/notifications/user/{userId}`
- **API Name:** getNotificationsByUser
- **Type:** REST / Synchronous
- **Path Variable:** `userId` (UUID)

**Request:**
None

**Response:**
*(List of NotificationResponse)*

---

### `GET` `/api/notifications/user/{userId}/paginated`
- **API Name:** getNotificationsByUserPaginated
- **Type:** REST / Synchronous
- **Query Parameters:** `page` (int), `size` (int), `sortBy` (String), `sortDir` (String)

**Request:**
None

**Response:**
*(Page of NotificationResponse)*

---

### `GET` `/api/notifications/status/{status}`
- **API Name:** getNotificationsByStatus
- **Type:** REST / Synchronous

**Request:**
None

**Response:**
*(List of NotificationResponse)*

---

### `GET` `/api/notifications/type/{type}`
- **API Name:** getNotificationsByType
- **Type:** REST / Synchronous

**Request:**
None

**Response:**
*(List of NotificationResponse)*

---

### `GET` `/api/notifications/campaign/{campaignId}`
- **API Name:** getNotificationsByCampaign
- **Type:** REST / Synchronous

**Request:**
None

**Response:**
*(List of NotificationResponse)*

---

### `PUT` `/api/notifications/{notificationId}/cancel`
- **API Name:** cancelNotification
- **Type:** REST / Synchronous

**Request:**
None

**Response:**
```json
{
  "status": "String - CANCELLED",
  "notificationId": "UUID",
  "message": "String"
}
```

---

### `PUT` `/api/notifications/{notificationId}/opened`
- **API Name:** markNotificationAsOpened
- **Type:** REST / Synchronous

**Request:**
None

**Response:**
```json
{
  "status": "String - OPENED",
  "notificationId": "UUID",
  "openedAt": "DateTime"
}
```

---

### `PUT` `/api/notifications/{notificationId}/clicked`
- **API Name:** markNotificationAsClicked
- **Type:** REST / Synchronous

**Request:**
None

**Response:**
```json
{
  "status": "String - CLICKED",
  "notificationId": "UUID",
  "clickedAt": "DateTime"
}
```

---

### `GET` `/api/notifications/statistics`
- **API Name:** getNotificationStatistics
- **Type:** REST / Synchronous
- **Query Parameters:** `startDate` (DateTime), `endDate` (DateTime)

**Request:**
None

**Response:**
```json
{
  "totalSent": "Long",
  "deliveryRate": "Double",
  "openRate": "Double"
}
```
*(Inferred Map response)*

---

## CampaignController

### `POST` `/api/campaigns`
- **API Name:** createCampaign
- **Type:** REST / Synchronous
- **Request Headers:** `X-User-Id` (optional)

**Request:**
```json
{
  "name": "String - Required",
  "description": "String",
  "campaignType": "String - Required",
  "templateId": "UUID",
  "targetAudience": {},
  "targetUserIds": ["UUID"],
  "targetSegments": ["String"],
  "scheduledStartAt": "DateTime",
  "scheduledEndAt": "DateTime",
  "isActive": "Boolean - Default true",
  "isRecurring": "Boolean - Default false",
  "recurrencePattern": "String",
  "maxRecipients": "Integer",
  "priority": "String - Default NORMAL",
  "budgetLimit": "BigDecimal",
  "costPerNotification": "BigDecimal",
  "isAbTest": "Boolean - Default false",
  "abTestPercentage": "BigDecimal",
  "abVariant": "String",
  "contentVariations": [{}],
  "personalizationData": {},
  "tags": ["String"],
  "metadata": {},
  "trackingParams": {},
  "createdBy": "String"
}
```

**Response:**
```json
{
  "id": "UUID",
  "name": "String",
  "description": "String",
  "campaignType": "String",
  "status": "String",
  "templateId": "UUID",
  "targetAudience": {},
  "targetUserIds": ["UUID"],
  "targetSegments": ["String"],
  "scheduledStartAt": "DateTime",
  "scheduledEndAt": "DateTime",
  "startedAt": "DateTime",
  "completedAt": "DateTime",
  "cancelledAt": "DateTime",
  "isActive": "Boolean",
  "isRecurring": "Boolean",
  "recurrencePattern": "String",
  "maxRecipients": "Integer",
  "priority": "String",
  "budgetLimit": "BigDecimal",
  "costPerNotification": "BigDecimal",
  "totalRecipients": "Integer",
  "sentCount": "Integer",
  "deliveredCount": "Integer",
  "failedCount": "Integer",
  "openedCount": "Integer",
  "clickedCount": "Integer",
  "bouncedCount": "Integer",
  "unsubscribedCount": "Integer",
  "totalCost": "BigDecimal",
  "isAbTest": "Boolean",
  "abTestPercentage": "BigDecimal",
  "abVariant": "String",
  "contentVariations": [{}],
  "personalizationData": {},
  "tags": ["String"],
  "metadata": {},
  "trackingParams": {},
  "createdBy": "String",
  "updatedBy": "String",
  "createdAt": "DateTime",
  "updatedAt": "DateTime",
  "deliveryRate": "Double",
  "openRate": "Double",
  "clickRate": "Double",
  "bounceRate": "Double",
  "unsubscribeRate": "Double",
  "isScheduled": "Boolean",
  "canStart": "Boolean",
  "isRunning": "Boolean",
  "isCompleted": "Boolean",
  "isCancelled": "Boolean"
}
```

---

### `PUT` `/api/campaigns/{campaignId}`
- **API Name:** updateCampaign
- **Type:** REST / Synchronous

**Request:**
*(Same as `createCampaign` request)*

**Response:**
*(Same as `createCampaign` CampaignResponse)*

---

### `GET` `/api/campaigns/{campaignId}`
- **API Name:** getCampaignById
- **Type:** REST / Synchronous

**Request:**
None

**Response:**
*(Same as `createCampaign` CampaignResponse)*

---

### `GET` `/api/campaigns/status/{status}`
- **API Name:** getCampaignsByStatus
- **Type:** REST / Synchronous

**Request:**
None

**Response:**
*(List of CampaignResponse)*

---

### `PUT` `/api/campaigns/{campaignId}/start`
- **API Name:** startCampaign
- **Type:** REST / Synchronous

**Request:**
None

**Response:**
```json
{
  "status": "String - STARTED",
  "campaignId": "UUID",
  "message": "String",
  "startedBy": "String",
  "startedAt": "DateTime"
}
```

---

## DeviceTokenController

### `POST` `/api/device-tokens/register`
- **API Name:** registerDeviceToken
- **Type:** REST / Synchronous
- **Request Headers:** `X-User-Agent`, `X-Forwarded-For`, `X-User-Id` (optional)

**Request:**
```json
{
  "userId": "UUID",
  "deviceToken": "String - Required",
  "platform": "String - Required",
  "deviceId": "String",
  "appVersion": "String",
  "osVersion": "String",
  "deviceModel": "String",
  "notificationEnabled": "Boolean - Default true",
  "topicSubscriptions": ["String"],
  "registeredFrom": "String",
  "userAgent": "String",
  "ipAddress": "String",
  "country": "String",
  "timezone": "String",
  "metadata": {}
}
```

**Response:**
```json
{
  "id": "UUID",
  "userId": "UUID",
  "deviceToken": "String - Masked",
  "snsEndpointArn": "String",
  "platform": "String",
  "deviceId": "String",
  "appVersion": "String",
  "osVersion": "String",
  "deviceModel": "String",
  "isActive": "Boolean",
  "isValid": "Boolean",
  "notificationEnabled": "Boolean",
  "subscribedTopics": ["String"],
  "errorCount": "Integer",
  "lastErrorMessage": "String",
  "lastErrorAt": "DateTime",
  "lastUsedAt": "DateTime",
  "lastValidatedAt": "DateTime",
  "registeredFrom": "String",
  "userAgent": "String",
  "ipAddress": "String",
  "country": "String",
  "timezone": "String",
  "createdAt": "DateTime",
  "updatedAt": "DateTime",
  "expiresAt": "DateTime",
  "metadata": {},
  "isExpired": "Boolean",
  "canReceiveNotifications": "Boolean"
}
```

---

### `PUT` `/api/device-tokens/{tokenId}`
- **API Name:** updateDeviceToken
- **Type:** REST / Synchronous

**Request:**
*(Same as `registerDeviceToken` request)*

**Response:**
*(Same as `registerDeviceToken` DeviceTokenResponse)*

---

### `GET` `/api/device-tokens/user/{userId}`
- **API Name:** getDeviceTokensByUser
- **Type:** REST / Synchronous

**Request:**
None

**Response:**
*(List of DeviceTokenResponse)*

---

## TemplateController

### `POST` `/api/templates`
- **API Name:** createTemplate
- **Type:** REST / Synchronous
- **Request Headers:** `X-User-Id` (optional)

**Request:**
```json
{
  "name": "String - Required",
  "templateType": "String - Required",
  "description": "String",
  "subjectTemplate": "String",
  "titleTemplate": "String",
  "contentTemplate": "String - Required",
  "htmlTemplate": "String",
  "smsTemplate": "String",
  "pushTemplate": "String",
  "variables": ["String"],
  "sampleData": {},
  "isActive": "Boolean - Default true",
  "isDefault": "Boolean - Default false",
  "language": "String - Default 'en'",
  "category": "String",
  "tags": ["String"],
  "createdBy": "String"
}
```

**Response:**
```json
{
  "id": "UUID",
  "name": "String",
  "templateType": "String",
  "description": "String",
  "subjectTemplate": "String",
  "titleTemplate": "String",
  "contentTemplate": "String",
  "htmlTemplate": "String",
  "smsTemplate": "String",
  "pushTemplate": "String",
  "variables": ["String"],
  "sampleData": {},
  "isActive": "Boolean",
  "isDefault": "Boolean",
  "version": "Integer",
  "language": "String",
  "category": "String",
  "tags": ["String"],
  "usageCount": "Long",
  "lastUsedAt": "DateTime",
  "createdBy": "String",
  "updatedBy": "String",
  "createdAt": "DateTime",
  "updatedAt": "DateTime"
}
```

---

### `PUT` `/api/templates/{templateId}`
- **API Name:** updateTemplate
- **Type:** REST / Synchronous

**Request:**
*(Same as `createTemplate` request)*

**Response:**
*(Same as `createTemplate` TemplateResponse)*

---

### `GET` `/api/templates/{templateId}`
- **API Name:** getTemplateById
- **Type:** REST / Synchronous

**Request:**
None

**Response:**
*(Same as `createTemplate` TemplateResponse)*

---

## AdminController

### `GET` `/api/admin/overview`
- **API Name:** getSystemOverview
- **Type:** REST / Synchronous

**Request:**
None

**Response:**
```json
{
  "system": {
    "applicationName": "String",
    "serverPort": "String",
    "timestamp": "DateTime",
    "version": "String",
    "environment": "String"
  },
  "serviceHealth": {
    "email": {},
    "sms": {},
    "push": {}
  },
  "last24Hours": {},
  "templates": {},
  "devices": {}
}
```

---

### `POST` `/api/admin/test/notification`
- **API Name:** sendTestNotification
- **Type:** REST / Synchronous
- **Query Parameters:** `type` (String), `recipient` (String), `message` (String)

**Request:**
None

**Response:**
```json
{
  "testType": "String",
  "timestamp": "DateTime",
  "sentBy": "String",
  "status": "String",
  "recipient": "String"
}
```
