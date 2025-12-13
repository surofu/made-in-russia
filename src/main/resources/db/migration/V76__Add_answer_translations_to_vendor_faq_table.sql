alter table vendor_faq
    add column if not exists answer_translations hstore;

update vendor_faq
set answer_translations = ''
where answer_translations is null;

alter table vendor_faq
    alter column answer_translations set not null;