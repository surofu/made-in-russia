alter table product_prices
    alter column unit_translations drop default;

alter table product_prices
    alter column unit_translations type jsonb using hstore_to_jsonb(unit_translations);
