create table if not exists similar_products
(
    parent_product_id  bigint not null,
    similar_product_id bigint not null,

    constraint fk_similar_products_parent_product_id foreign key (parent_product_id) references products (id),
    constraint fk_similar_products_similar_product_id foreign key (similar_product_id) references products (id)
);