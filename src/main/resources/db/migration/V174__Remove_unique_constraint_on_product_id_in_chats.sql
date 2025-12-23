-- Remove unique constraint on product_id to allow multiple chats per product
-- Each buyer can now have their own separate chat with the seller for the same product

ALTER TABLE chats DROP CONSTRAINT IF EXISTS uk_chats_product;

-- Add comment explaining the change
COMMENT ON TABLE chats IS 'Chats between users for specific products. Each buyer-seller pair for a product has a separate chat.';