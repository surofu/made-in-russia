alter table product_characteristics
    add column name_translations hstore;

update product_characteristics
set name_translations = ''
where name_translations is null;

alter table product_characteristics
    alter column name_translations set not null;