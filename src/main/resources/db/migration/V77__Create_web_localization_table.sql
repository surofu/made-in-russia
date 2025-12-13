create table if not exists web_localization
(
    id                     bigserial primary key,
    language_code          varchar(6) unique not null,
    content                jsonb             not null,
    creation_date          timestamptz       not null default now(),
    last_modification_date timestamptz       not null default now()
);