create table if not exists advertisements
(
    id                     bigserial primary key,
    title                  varchar(255) not null,
    subtitle               varchar(255),
    image_url              text         not null check ( length(image_url) <= 20000 ),
    creation_date          timestamptz  not null default now(),
    last_modification_date timestamptz  not null default now()
);