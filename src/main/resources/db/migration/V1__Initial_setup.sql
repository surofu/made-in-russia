create table if not exists categories
(
    id                     bigserial primary key,
    name                   varchar(255) not null unique,
    creation_date          timestamptz  not null default now(),
    last_modification_date timestamptz  not null default now()
);

create table if not exists delivery_methods
(
    id                     bigserial primary key,
    name                   varchar(255) not null unique,
    creation_date          timestamptz  not null default now(),
    last_modification_date timestamptz  not null default now()
);

create table if not exists products
(
    id                     bigserial primary key,
    category_id            bigint         not null,
    delivery_method_id     bigint         not null,
    title                  varchar(255)   not null,
    price                  decimal(10, 2) not null check ( price > 0 ),
    discount               decimal(5, 2)  not null check ( discount >= 0 and discount <= 100 ),
    image_url              text           not null,
    creation_date          timestamptz    not null default now(),
    last_modification_date timestamptz    not null default now(),

    constraint fk_products_category_id foreign key (category_id) references categories (id),
    constraint fk_products_delivery_method_id foreign key (delivery_method_id) references delivery_methods (id),
    constraint check_products_discount_price check ( price * (1 - discount / 100) >= 0)
);