alter table vendor_views
    drop constraint fk_product_views_vendor_id;

alter table vendor_views
    rename column vendor_id to vendor_details_id;

alter table vendor_views
    add constraint fk_product_views_vendor_details_id foreign key (vendor_details_id) references vendor_details (id);