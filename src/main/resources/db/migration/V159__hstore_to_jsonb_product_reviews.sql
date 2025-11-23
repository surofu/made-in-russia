alter table product_reviews
    alter column content_translations drop default;

alter table product_reviews
    alter column content_translations type jsonb using hstore_to_jsonb(content_translations);
