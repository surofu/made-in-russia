alter table categories
    alter column name_translations drop default;

alter table categories
    alter column name_translations type jsonb using hstore_to_jsonb(name_translations);