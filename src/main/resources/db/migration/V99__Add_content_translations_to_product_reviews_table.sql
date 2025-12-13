alter table product_reviews
    add column content_translations hstore;

update product_reviews
set content_translations = ''
where content_translations is null;

alter table product_reviews
    alter column content_translations set not null;