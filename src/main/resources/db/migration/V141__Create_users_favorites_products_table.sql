create table users_favorite_products
(
    id         bigserial primary key,
    user_id    bigint not null,
    product_id bigint not null
);