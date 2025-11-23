alter table users
    alter column login_transliteration drop default;

alter table users
    alter column login_transliteration type jsonb using hstore_to_jsonb(login_transliteration);
