create table if not exists product_reviews
(
    id                     bigserial primary key,
    user_id                bigint      not null,
    product_id             bigint      not null,
    text                   text        not null,
    rating                 smallint    not null,
    creation_date          timestamptz not null default now(),
    last_modification_date timestamptz not null default now(),

    constraint fk_product_reviews_user_id foreign key (user_id) references users (id),
    constraint fk_product_reviews_product_id foreign key (product_id) references products (id),
    constraint check_product_reviews_text check ( length(text) < 20000 ),
    constraint check_product_reviews_rating check ( rating >= 0 and rating <= 5 )
);