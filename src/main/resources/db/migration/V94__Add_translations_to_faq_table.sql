alter table faq
    add column if not exists question_translations hstore,
    add column if not exists answer_translations   hstore;

update faq
set question_translations = ''
where question_translations is null;

update faq
set answer_translations = ''
where answer_translations is null;

alter table faq
    alter column question_translations set not null,
    alter column answer_translations set not null;

