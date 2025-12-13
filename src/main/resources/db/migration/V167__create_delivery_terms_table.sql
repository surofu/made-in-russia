create table delivery_terms
(
    id          bigserial primary key,
    code        varchar(50)  not null unique,
    name        varchar(255) not null,
    description text
);

create unique index idx_delivery_terms_code on delivery_terms (code);

create table products_delivery_terms
(
    id                       bigserial primary key,
    product_id               bigint not null,
    delivery_term_id bigint not null,

    constraint fk_products_delivery_terms_product_id foreign key (product_id) references products (id),
    constraint fk_products_delivery_terms_delivery_term_id foreign key (delivery_term_id) references delivery_terms (id)
);

create index idx_products_delivery_terms_product_id on products_delivery_terms (product_id);
create index idx_products_delivery_terms_delivery_term_id on products_delivery_terms (delivery_term_id);
create unique index idx_products_delivery_terms_product_id_delivery_term_id on products_delivery_terms (product_id, delivery_term_id);