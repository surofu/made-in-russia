create table if not exists product_prices
(
    id                     bigserial primary key,
    product_id             bigint         not null,
    "from"                 int            not null check ( "from" > 0 ),
    "to"                   int            not null check ( "to" > 0 ),
    currency               varchar(255)   not null,
    unit                   varchar(255)   not null,
    original_price         decimal(10, 2) not null check ( original_price >= 0 ),
    discount               decimal(5, 2)  not null check ( discount >= 0 and discount <= 100 ),
    minimum_order_quantity int            not null check ( minimum_order_quantity >= 0 ),
    expiry_date            timestamptz    not null,
    creation_date          timestamptz    not null default now(),
    last_modification_date timestamptz    not null default now(),

    constraint fk_product_prices_product_id foreign key (product_id) references products (id)
);