alter table products_delivery_methods
    drop constraint fk_products_delivery_methods_product_id;

alter table products_delivery_methods
    add constraint fk_products_delivery_methods_product_id
        foreign key (product_id) references products (id)
            on update cascade
            on delete cascade;

alter table products_delivery_terms
    drop constraint fk_products_delivery_terms_product_id;

alter table products_delivery_terms
    add constraint fk_products_delivery_terms_product_id
        foreign key (product_id) references products (id)
            on update cascade
            on delete cascade;

alter table product_media
    drop constraint fk_product_media_product_id;

alter table product_media
    add constraint fk_product_media_product_id
        foreign key (product_id) references products (id)
            on update cascade
            on delete cascade;

alter table product_characteristics
    drop constraint product_characteristics;

alter table product_characteristics
    add constraint fk_product_characteristics_product_id
        foreign key (product_id) references products (id)
            on update cascade
            on delete cascade;

alter table product_package_options
    drop constraint fk_product_package_options_product_id;

alter table product_package_options
    add constraint fk_product_package_options_product_id
        foreign key (product_id) references products (id)
            on update cascade
            on delete cascade;

alter table product_delivery_method_details
    drop constraint fk_product_delivery_method_details_product_id;

alter table product_delivery_method_details
    add constraint fk_product_delivery_method_details_product_id
        foreign key (product_id) references products (id)
            on update cascade
            on delete cascade;

alter table product_faq
    drop constraint fk_product_faq_product_id;

alter table product_faq
    add constraint fk_product_faq_product_id
        foreign key (product_id) references products (id)
            on update cascade
            on delete cascade;

alter table product_prices
    drop constraint fk_product_prices_product_id;

alter table product_prices
    add constraint fk_product_prices_product_id
        foreign key (product_id) references products (id)
            on update cascade
            on delete cascade;

alter table product_reviews
    drop constraint fk_product_reviews_product_id;

alter table product_reviews
    add constraint fk_product_reviews_product_id
        foreign key (product_id) references products (id)
            on update cascade
            on delete cascade;

alter table product_review_media
    drop constraint fk_product_review_media_product_review_id;

alter table product_review_media
    add constraint fk_product_review_media_product_review_id
        foreign key (product_review_id) references product_reviews (id)
            on update cascade
            on delete cascade;

alter table similar_products
    drop constraint fk_similar_products_parent_product_id;

alter table similar_products
    add constraint fk_similar_products_parent_product_id
        foreign key (parent_product_id) references products (id)
            on update cascade
            on delete cascade;

alter table similar_products
    drop constraint fk_similar_products_parent_product_id;

alter table similar_products
    add constraint fk_similar_products_parent_product_id
        foreign key (similar_product_id) references products (id)
            on update cascade
            on delete cascade;