alter table products
    drop column if exists delivery_method_id;

create table if not exists products_delivery_types
(
    id                 bigserial primary key,
    product_id         bigint not null,
    delivery_method_id bigint not null,

    constraint fx_products_delivery_types_product_id foreign key (product_id) references products (id),
    constraint fx_products_delivery_types_delivery_method_id foreign key (delivery_method_id) references delivery_methods (id)
);