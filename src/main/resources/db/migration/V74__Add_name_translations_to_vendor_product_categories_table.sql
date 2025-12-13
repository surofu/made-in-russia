alter table vendor_product_categories
    add column if not exists name_translations hstore;

update vendor_product_categories
set name_translations = ''
where name_translations is null;

alter table vendor_product_categories
    alter column name_translations set not null;