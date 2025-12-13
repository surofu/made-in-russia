alter table product_vendor_details
    add column main_description_translations hstore;

update product_vendor_details
set main_description_translations = ''
where main_description_translations is null;

alter table product_vendor_details
    alter column main_description_translations set not null;