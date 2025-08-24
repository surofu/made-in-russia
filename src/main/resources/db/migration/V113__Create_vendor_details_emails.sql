create table vendor_details_emails
(
    id                     bigserial primary key,
    vendor_details_id      bigint       not null,
    email                  varchar(255) not null,
    creation_date          timestamptz  not null default now(),
    last_modification_date timestamptz  not null default now(),

    constraint fk_vendor_details_emails_vendor_details_id foreign key (vendor_details_id) references vendor_details (id)
);