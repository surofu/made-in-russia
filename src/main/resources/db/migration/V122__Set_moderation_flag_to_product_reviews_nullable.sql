alter table product_reviews
    alter column is_approved drop not null,
    alter column is_approved drop default;