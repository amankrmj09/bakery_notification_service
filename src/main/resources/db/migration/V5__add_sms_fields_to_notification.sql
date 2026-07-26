-- Add SMS support fields to the notifications table
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS recipient_phone VARCHAR(20);
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS sms_message_id VARCHAR(255);
