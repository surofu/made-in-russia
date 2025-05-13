alter table users
    add column if not exists phone_number varchar(255) not null default '',
    add column if not exists region       varchar(255) not null default '';