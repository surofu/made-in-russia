create table if not exists sessions
(
    id                     bigserial primary key,
    user_id                bigint       not null,
    device_id              text         not null unique,
    device_type            varchar(255) not null,
    browser                varchar(255) not null,
    os                     varchar(255) not null,
    ip_address             inet         not null,
    creation_date          timestamptz  not null default now(),
    last_modification_date timestamptz  not null default now(),
    last_login_date        timestamptz  not null default now(),

    constraint fk_sessions_user_id foreign key (user_id) references users (id)
);