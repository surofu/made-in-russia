create table if not exists product_review_media
(
    id                     bigserial primary key,
    product_id             bigint                    not null,
    media_type             media_type not null,
    mime_type              varchar(100)              not null,
    position               int                       not null default 0,
    url                    text                      not null,
    alt_text               varchar(255),
    creation_date          timestamptz               not null default now(),
    last_modification_date timestamptz               not null default now(),

    constraint fk_product_images_product_id foreign key (product_id) references products (id)
);