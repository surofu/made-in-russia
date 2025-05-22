create table if not exists product_questions
(
    id                     bigserial primary key,
    user_id                bigint      not null,
    product_id             bigint      not null,
    text                   text        not null,
    creation_date          timestamptz not null default now(),
    last_modification_date timestamptz not null default now(),

    constraint fk_product_questions_user_id foreign key (user_id) references users (id),
    constraint fk_product_questions_product_id foreign key (product_id) references products (id),
    constraint check_product_reviews_text check ( length(text) < 20000 )
);