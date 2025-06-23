create table if not exists product_vendor_details
(
    id                     bigserial primary key,
    product_id             bigint      not null,
    main_description       text        not null,
    further_description    text,
    creation_date          timestamptz not null default now(),
    last_modification_date timestamptz not null default now(),

    constraint fk_product_vendor_details_product_id foreign key (product_id) references products (id),
    constraint check_product_vendor_details_main_description check ( length(main_description) < 10000 ),
    constraint check_product_vendor_details_further_description check ( length(further_description) < 5000 )
);

revoke update (creation_date) on product_vendor_details from public;