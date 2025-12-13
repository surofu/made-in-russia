create table if not exists faq
(
    id                     bigserial primary key,
    question               text        not null check ( length(question) <= 2000 ),
    answer                 text        not null check ( length(answer) <= 20000 ),
    creation_date          timestamptz not null default now(),
    last_modification_date timestamptz not null default now()
);