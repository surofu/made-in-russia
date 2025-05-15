alter table sessions
    add column if not exists dtype varchar(255) not null default '';