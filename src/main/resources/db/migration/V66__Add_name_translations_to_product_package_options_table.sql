alter table product_package_options
    add column name_translations hstore;

update product_package_options
set name_translations = ''
where name_translations is null;

alter table product_package_options
    alter column name_translations set not null;