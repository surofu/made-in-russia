alter table faq
    alter column question_translations drop default,
    alter column answer_translations drop default;

alter table faq
    alter column question_translations type jsonb using hstore_to_jsonb(question_translations),
    alter column answer_translations type jsonb using hstore_to_jsonb(answer_translations);
