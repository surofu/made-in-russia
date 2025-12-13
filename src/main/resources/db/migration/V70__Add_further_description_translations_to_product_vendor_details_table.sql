alter table product_vendor_details
    add column further_description_translations hstore;

update product_vendor_details
set further_description_translations = ''
where further_description_translations is null;

alter table product_vendor_details
    alter column further_description_translations set not null;