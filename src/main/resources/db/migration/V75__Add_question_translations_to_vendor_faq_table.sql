alter table vendor_faq
    add column if not exists question_translations hstore;

update vendor_faq
set question_translations = ''
where question_translations is null;

alter table vendor_faq
    alter column question_translations set not null;