

ALTER TABLE chat_messages DROP CONSTRAINT IF EXISTS check_content_not_empty;


ALTER TABLE chat_messages ALTER COLUMN content DROP NOT NULL;

COMMENT ON COLUMN chat_messages.content IS 'Message text content. Can be NULL if message has attachments.';
