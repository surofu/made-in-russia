alter table delivery_methods
    alter column name_translations drop default;

alter table delivery_methods
    alter column name_translations type jsonb using hstore_to_jsonb(name_translations);