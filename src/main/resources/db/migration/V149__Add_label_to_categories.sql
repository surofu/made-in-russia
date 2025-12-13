alter table categories
    add column label              varchar(255),
    add column label_translations jsonb;

update categories
set label              = name,
    label_translations = name_translations
where label is null;