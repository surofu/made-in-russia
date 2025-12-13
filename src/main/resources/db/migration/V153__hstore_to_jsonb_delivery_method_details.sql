alter table product_delivery_method_details
    alter column name_translations drop default,
    alter column value_translations drop default;

alter table product_delivery_method_details
    alter column name_translations type jsonb using hstore_to_jsonb(name_translations),
    alter column value_translations type jsonb using hstore_to_jsonb(value_translations);
