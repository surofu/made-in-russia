alter table product_characteristics
    add column value_translations hstore;

update product_characteristics
set value_translations = ''
where value_translations is null;

alter table product_characteristics
    alter column value_translations set not null;