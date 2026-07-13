ALTER TABLE notifications DROP COLUMN template_id;
ALTER TABLE notifications ADD COLUMN template_id BIGINT;
CREATE INDEX idx_notification_template ON notifications (template_id);
