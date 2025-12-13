alter table product_vendor_details_media
    add column if not exists position int;

update product_vendor_details_media
set position = 0
where position is null;

alter table product_vendor_details_media
    alter column position set not null;