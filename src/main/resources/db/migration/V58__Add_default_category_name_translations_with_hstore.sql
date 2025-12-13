update categories
set name_translations = ''
where name_translations is null;


alter table categories
    alter column name_translations set not null;