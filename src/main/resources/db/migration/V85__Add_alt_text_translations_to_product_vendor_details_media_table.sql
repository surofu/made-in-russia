alter table product_vendor_details_media
    add column if not exists alt_text_translations hstore not null default '';