alter table product_faq
    add column answer_translations hstore;

update product_faq
set answer_translations = ''
where answer_translations is null;

alter table product_faq
    alter column answer_translations set not null;