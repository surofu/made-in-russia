alter table vendor_countries
    add column name_translations hstore;

update vendor_countries
set name_translations = ''
where name_translations is null;

alter table vendor_countries
    alter column name_translations set not null;