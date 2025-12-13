alter table vendor_details
    add column address text;

update vendor_details
set address = ''
where address is null;

alter table vendor_details
    alter column address set not null;