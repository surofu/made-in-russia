alter table vendor_details
    add column if not exists payment_details varchar(255) not null default 'ЕРИП 12345АБВГД67890';

alter table vendor_details
    alter column payment_details drop default;