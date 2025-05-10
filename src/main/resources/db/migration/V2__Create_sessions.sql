create table if not exists sessions
(
    id                     bigserial primary key,
    user_id                bigint      not null,
    device_name            varchar(255),
    browser                varchar(255),
    os                     varchar(255),
    ipAddress              inet,
    creation_date          timestamptz not null default now(),
    last_modification_date timestamptz not null default now(),
    last_login_date        timestamptz not null default now(),

    constraint fk_sessions_user_id foreign key (user_id) references users (id)
);