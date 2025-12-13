alter table products
    add column title_translations               hstore,
    add column main_description_translations    hstore,
    add column further_description_translations hstore;

update products
set title_translations = ''
where title_translations is null;

update products
set main_description_translations = ''
where main_description_translations is null;

update products
set further_description_translations = ''
where further_description_translations is null;

alter table products
    alter column title_translations set not null,
    alter column main_description_translations set not null,
    alter column further_description_translations set not null;