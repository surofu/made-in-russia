alter table product_prices
    drop column if exists minimum_order_quantity,
    drop column if exists discount_expiration_date;