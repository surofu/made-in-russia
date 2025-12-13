alter table product_delivery_method_details
    add column name_translations hstore;

update product_delivery_method_details
set name_translations = ''
where name_translations is null;

alter table product_delivery_method_details
    alter column name_translations set not null;