alter table vendor_views
    add unique (user_id, vendor_details_id);