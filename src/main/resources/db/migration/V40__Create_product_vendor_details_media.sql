create table if not exists product_vendor_details_media
(
    id                        bigserial primary key,
    product_vendor_details_id bigint      not null,
    url                       text        not null,
    creation_date             timestamptz not null default now(),
    last_modification_date    timestamptz not null default now(),

    constraint fk_product_vendor_details_media_product_vendor_details_id foreign key (product_vendor_details_id)
        references product_vendor_details (id),
    constraint check_product_vendor_details_media check ( length(url) < 10000 )
);

revoke update (creation_date) on product_vendor_details_media from public;