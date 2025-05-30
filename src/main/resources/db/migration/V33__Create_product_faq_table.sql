drop table if exists product_questions;

create table if not exists product_faq
(
    id                     bigserial primary key,
    product_id             bigint      not null,
    question               text not null,
    answer                 text not null,
    creation_date          timestamptz not null default now(),
    last_modification_date timestamptz not null default now(),

    constraint fk_product_faq_product_id foreign key (product_id) references products (id)
);