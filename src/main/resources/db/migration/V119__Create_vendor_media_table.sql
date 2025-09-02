create table if not exists vendor_media
(
    id                     bigserial primary key,
    vendor_details_id      bigint       not null,
    media_type             varchar(255) not null,
    mime_type              varchar(255) not null,
    url                    text         not null check ( length(url) <= 20000 ),
    position               int          not null default 0,
    creation_date          timestamptz  not null default now(),
    last_modification_date timestamptz  not null default now(),

    foreign key (vendor_details_id) references vendor_details (id)
);

revoke update (last_modification_date) on vendor_media from public;

create unique index idx_vendor_media_vendor_details_id on vendor_media (id, vendor_details_id);