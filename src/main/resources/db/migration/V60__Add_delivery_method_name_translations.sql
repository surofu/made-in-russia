alter table delivery_methods
    add column name_translations hstore;

update delivery_methods
set name_translations = ''
where name_translations is null;

alter table delivery_methods
    alter column name_translations set not null;