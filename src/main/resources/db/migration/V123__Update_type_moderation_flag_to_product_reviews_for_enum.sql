alter table product_reviews
    alter column is_approved type varchar(255),
    alter column is_approved set not null,
    alter column is_approved set default 'PENDING';