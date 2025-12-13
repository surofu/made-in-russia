alter table product_media
    alter column alt_text_translations drop default;

alter table product_media
    alter column alt_text_translations type jsonb using hstore_to_jsonb(alt_text_translations);
