alter table product_vendor_details_media
    add column if not exists media_type media_type;

update product_vendor_details_media
set media_type = 'IMAGE'
where media_type is null;

alter table product_vendor_details_media
    alter column media_type set not null;