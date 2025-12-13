create table if not exists product_package_options
(
    id                     bigserial primary key,
    product_id             bigint         not null,
    name                   varchar(255)   not null,
    price                  decimal(10, 2) not null,
    creation_date          timestamptz    not null default now(),
    last_modification_date timestamptz    not null default now(),

    constraint fk_product_package_options_product_id foreign key (product_id) references products (id)
);

revoke update (creation_date) on product_package_options from public;