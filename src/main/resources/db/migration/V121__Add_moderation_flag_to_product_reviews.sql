alter table product_reviews
    add column is_approved boolean default false;

update product_reviews
set is_approved = true;