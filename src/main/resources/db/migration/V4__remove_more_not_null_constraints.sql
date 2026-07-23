ALTER TABLE notifications ALTER COLUMN bounce_count DROP NOT NULL;
ALTER TABLE notifications ALTER COLUMN retry_count DROP NOT NULL;
ALTER TABLE notifications ALTER COLUMN max_retry_count DROP NOT NULL;
