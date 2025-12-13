alter table products
    add column approve_status varchar(255);

update products
set approve_status = 'APPROVED'
where approve_status is null;

alter table products
    alter column approve_status set not null,
    alter column approve_status set default 'PENDING';