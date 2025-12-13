alter table categories
    add column title              varchar(255),
    add column title_translations jsonb;

update categories
set title              = name,
    title_translations = name_translations
where title is null;