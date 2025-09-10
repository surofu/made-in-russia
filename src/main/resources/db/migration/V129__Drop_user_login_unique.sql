alter table users
    drop constraint uk_users_login;

drop index idx_users_login;
create index idx_users_login on users (login);