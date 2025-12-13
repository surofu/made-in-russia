alter table product_media
    drop constraint fk_product_media_product_id;
alter table product_characteristics
    drop constraint fk_product_characteristics_product_id;
alter table product_reviews
    drop constraint fk_product_reviews_product_id;
alter table product_review_media
    drop constraint fk_product_review_media_product_review_id;
alter table product_faq
    drop constraint fk_product_faq_product_id;
alter table product_prices
    drop constraint fk_product_prices_product_id;
alter table product_delivery_method_details
    drop constraint fk_product_delivery_method_details_product_id;
alter table product_package_options
    drop constraint fk_product_package_options_product_id;

alter table product_media
    add constraint fk_product_media_product_id
        foreign key (product_id) references products (id) on delete cascade;
alter table product_characteristics
    add constraint product_characteristics
        foreign key (product_id) references products (id) on delete cascade;
alter table product_reviews
    add constraint fk_product_reviews_product_id
        foreign key (product_id) references products (id) on delete cascade;
alter table product_review_media
    add constraint fk_product_review_media_product_review_id
        foreign key (product_review_id) references product_reviews (id) on delete cascade;
alter table product_faq
    add constraint fk_product_faq_product_id
        foreign key (product_id) references products (id) on delete cascade;
alter table product_prices
    add constraint fk_product_prices_product_id
        foreign key (product_id) references products (id) on delete cascade;
alter table product_delivery_method_details
    add constraint fk_product_delivery_method_details_product_id
        foreign key (product_id) references products (id) on delete cascade;
alter table product_package_options
    add constraint fk_product_package_options_product_id
        foreign key (product_id) references products (id) on delete cascade;