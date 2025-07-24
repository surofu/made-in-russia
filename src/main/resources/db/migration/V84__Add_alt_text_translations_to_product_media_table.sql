alter table product_media
    add column if not exists alt_text_translations hstore not null default '';