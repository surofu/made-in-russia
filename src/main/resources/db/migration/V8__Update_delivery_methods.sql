alter table delivery_methods
    add column if not exists product_id bigint;