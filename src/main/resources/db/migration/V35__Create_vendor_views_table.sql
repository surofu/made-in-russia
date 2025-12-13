create table if not exists vendor_views
(
    id                     bigserial primary key,
    vendor_id              bigint      not null,
    user_id                bigint      not null,
    creation_date          timestamptz not null default now(),
    last_modification_date timestamptz not null default now(),

    constraint fk_product_views_vendor_id foreign key (vendor_id) references users (id),
    constraint fk_product_views_user_id foreign key (user_id) references users (id)
);

revoke update (creation_date) on vendor_views from public;

create index if not exists idx_vendor_views_vendor_id on vendor_views (vendor_id);
create index if not exists idx_vendor_views_user_id on vendor_views (user_id);
