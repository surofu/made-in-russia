alter table advertisements
    add column if not exists third_text_translations hstore;

update advertisements
set third_text_translations = ''
where advertisements.third_text_translations is null;

alter table advertisements
    alter column third_text_translations set not null;