alter table product_delivery_method_details
    add column value_translations hstore;

update product_delivery_method_details
set value_translations = ''
where value_translations is null;

alter table product_delivery_method_details
    alter column value_translations set not null;