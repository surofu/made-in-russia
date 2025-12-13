alter table advertisements
    alter column title_translations drop default,
    alter column subtitle_translations drop default,
    alter column third_text_translations drop default;

alter table advertisements
    alter column title_translations type jsonb using hstore_to_jsonb(title_translations),
    alter column subtitle_translations type jsonb using hstore_to_jsonb(subtitle_translations),
    alter column third_text_translations type jsonb using hstore_to_jsonb(third_text_translations);