alter table product_faq
    add column question_translations hstore;

update product_faq
set question_translations = ''
where question_translations is null;

alter table product_faq
    alter column question_translations set not null;