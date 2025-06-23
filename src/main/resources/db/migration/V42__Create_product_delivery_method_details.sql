create table if not exists product_delivery_method_details
(
    id                     bigserial primary key,
    product_id             bigint       not null,
    name                   varchar(255) not null,
    value                  varchar(255) not null,
    creation_date          timestamptz  not null default now(),
    last_modification_date timestamptz  not null default now(),

    constraint fk_product_delivery_method_details_product_id foreign key (product_id) references products (id)
);

revoke update (creation_date) on product_delivery_method_details from public;