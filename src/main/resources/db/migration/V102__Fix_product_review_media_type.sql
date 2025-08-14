alter table product_review_media
    alter column media_type type varchar(255) using media_type::varchar(255);