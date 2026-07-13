CREATE TABLE device_tokens
(
    id                   UUID         NOT NULL,
    user_id              UUID,
    device_token         VARCHAR(500) NOT NULL,
    sns_endpoint_arn     VARCHAR(500),
    platform             VARCHAR(20)  NOT NULL,
    device_id            VARCHAR(255),
    app_version          VARCHAR(20),
    os_version           VARCHAR(20),
    device_model         VARCHAR(100),
    is_active            BOOLEAN      NOT NULL,
    is_valid             BOOLEAN      NOT NULL,
    notification_enabled BOOLEAN      NOT NULL,
    subscribed_topics    TEXT,
    error_count          INTEGER      NOT NULL,
    last_error_message   TEXT,
    last_error_at        TIMESTAMP WITHOUT TIME ZONE,
    last_used_at         TIMESTAMP WITHOUT TIME ZONE,
    last_validated_at    TIMESTAMP WITHOUT TIME ZONE,
    registered_from      VARCHAR(50),
    user_agent           TEXT,
    ip_address           VARCHAR(45),
    country              VARCHAR(2),
    timezone             VARCHAR(50),
    metadata             TEXT,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE,
    expires_at           TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_device_tokens PRIMARY KEY (id)
);

CREATE TABLE notification_campaigns
(
    id                    UUID         NOT NULL,
    name                  VARCHAR(255) NOT NULL,
    description           VARCHAR(500),
    campaign_type         VARCHAR(255) NOT NULL,
    status                VARCHAR(255) NOT NULL,
    template_id           UUID,
    target_audience       TEXT,
    target_user_ids       TEXT,
    target_segments       TEXT,
    scheduled_start_at    TIMESTAMP WITHOUT TIME ZONE,
    scheduled_end_at      TIMESTAMP WITHOUT TIME ZONE,
    started_at            TIMESTAMP WITHOUT TIME ZONE,
    completed_at          TIMESTAMP WITHOUT TIME ZONE,
    cancelled_at          TIMESTAMP WITHOUT TIME ZONE,
    is_active             BOOLEAN      NOT NULL,
    is_recurring          BOOLEAN      NOT NULL,
    recurrence_pattern    VARCHAR(100),
    max_recipients        INTEGER,
    priority              VARCHAR(20),
    budget_limit          DECIMAL(10, 2),
    cost_per_notification DECIMAL(6, 4),
    total_recipients      INTEGER      NOT NULL,
    sent_count            INTEGER      NOT NULL,
    delivered_count       INTEGER      NOT NULL,
    failed_count          INTEGER      NOT NULL,
    opened_count          INTEGER      NOT NULL,
    clicked_count         INTEGER      NOT NULL,
    bounced_count         INTEGER      NOT NULL,
    unsubscribed_count    INTEGER      NOT NULL,
    total_cost            DECIMAL(10, 2),
    is_ab_test            BOOLEAN      NOT NULL,
    ab_test_percentage    DECIMAL(5, 2),
    ab_variant            VARCHAR(10),
    content_variations    TEXT,
    personalization_data  TEXT,
    tags                  TEXT,
    metadata              TEXT,
    tracking_params       TEXT,
    created_by            VARCHAR(100),
    updated_by            VARCHAR(100),
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_notification_campaigns PRIMARY KEY (id)
);

CREATE TABLE notification_templates
(
    id               UUID         NOT NULL,
    name             VARCHAR(100) NOT NULL,
    template_type    VARCHAR(255) NOT NULL,
    description      VARCHAR(500),
    subject_template VARCHAR(255),
    title_template   VARCHAR(500),
    content_template TEXT         NOT NULL,
    html_template    TEXT,
    sms_template     TEXT,
    push_template    TEXT,
    variables        TEXT,
    sample_data      TEXT,
    is_active        BOOLEAN      NOT NULL,
    is_default       BOOLEAN      NOT NULL,
    version          INTEGER      NOT NULL,
    language         VARCHAR(10),
    category         VARCHAR(50),
    tags             TEXT,
    usage_count      BIGINT       NOT NULL,
    last_used_at     TIMESTAMP WITHOUT TIME ZONE,
    created_by       VARCHAR(100),
    updated_by       VARCHAR(100),
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_notification_templates PRIMARY KEY (id)
);

CREATE TABLE notifications
(
    id                  UUID         NOT NULL,
    user_id             UUID,
    recipient_email     VARCHAR(255),
    recipient_phone     VARCHAR(20),
    recipient_name      VARCHAR(100),
    type                VARCHAR(255) NOT NULL,
    status              VARCHAR(255) NOT NULL,
    priority            VARCHAR(255) NOT NULL,
    template_id         UUID,
    campaign_id         UUID,
    title               VARCHAR(500) NOT NULL,
    content             TEXT,
    html_content        TEXT,
    subject             VARCHAR(255),
    push_token          VARCHAR(255),
    sns_endpoint_arn    VARCHAR(500),
    sns_message_id      VARCHAR(255),
    platform            VARCHAR(20),
    twilio_message_sid  VARCHAR(255),
    email_message_id    VARCHAR(255),
    bounce_count        INTEGER      NOT NULL,
    retry_count         INTEGER      NOT NULL,
    max_retry_count     INTEGER      NOT NULL,
    scheduled_at        TIMESTAMP WITHOUT TIME ZONE,
    sent_at             TIMESTAMP WITHOUT TIME ZONE,
    delivered_at        TIMESTAMP WITHOUT TIME ZONE,
    failed_at           TIMESTAMP WITHOUT TIME ZONE,
    opened_at           TIMESTAMP WITHOUT TIME ZONE,
    clicked_at          TIMESTAMP WITHOUT TIME ZONE,
    error_message       TEXT,
    error_code          VARCHAR(50),
    last_error_at       TIMESTAMP WITHOUT TIME ZONE,
    metadata            TEXT,
    tracking_data       TEXT,
    related_entity_type VARCHAR(50),
    related_entity_id   UUID,
    source              VARCHAR(50),
    triggered_by        VARCHAR(100),
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    expires_at          TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

ALTER TABLE notification_templates
    ADD CONSTRAINT uc_notification_templates_name UNIQUE (name);

CREATE INDEX idx_campaign_active ON notification_campaigns (is_active);

CREATE INDEX idx_campaign_created ON notification_campaigns (created_at);

CREATE INDEX idx_campaign_scheduled ON notification_campaigns (scheduled_start_at);

CREATE INDEX idx_campaign_status ON notification_campaigns (status);

CREATE INDEX idx_campaign_type ON notification_campaigns (campaign_type);

CREATE INDEX idx_device_active ON device_tokens (is_active);

CREATE INDEX idx_device_created ON device_tokens (created_at);

CREATE INDEX idx_device_endpoint ON device_tokens (sns_endpoint_arn);

CREATE INDEX idx_device_platform ON device_tokens (platform);

CREATE INDEX idx_device_token ON device_tokens (device_token);

CREATE INDEX idx_device_user ON device_tokens (user_id);

CREATE INDEX idx_device_user_platform ON device_tokens (user_id, platform);

CREATE INDEX idx_notification_campaign ON notifications (campaign_id);

CREATE INDEX idx_notification_created ON notifications (created_at);

CREATE INDEX idx_notification_priority ON notifications (priority);

CREATE INDEX idx_notification_scheduled ON notifications (scheduled_at);

CREATE INDEX idx_notification_sent ON notifications (sent_at);

CREATE INDEX idx_notification_status ON notifications (status);

CREATE INDEX idx_notification_status_scheduled ON notifications (status, scheduled_at);

CREATE INDEX idx_notification_template ON notifications (template_id);

CREATE INDEX idx_notification_type ON notifications (type);

CREATE INDEX idx_notification_user ON notifications (user_id);

CREATE INDEX idx_notification_user_type ON notifications (user_id, type);

CREATE INDEX idx_template_active ON notification_templates (is_active);

CREATE INDEX idx_template_created ON notification_templates (created_at);

CREATE INDEX idx_template_name ON notification_templates (name);

CREATE INDEX idx_template_type ON notification_templates (template_type);

CREATE INDEX idx_template_type_active ON notification_templates (template_type, is_active);
