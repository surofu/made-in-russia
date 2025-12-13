drop index idx_vendor_views_vendor_id;

create index if not exists idx_vendor_views_vendor_details_id on vendor_views (vendor_details_id);