alter table product_package_options
    add column value_translations hstore;

update product_package_options
set value_translations = ''
where value_translations is null;

alter table product_package_options
    alter column value_translations set not null;