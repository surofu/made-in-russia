alter table users
    add constraint uk_users_telegram_user_id unique (telegram_user_id);

create index idx_users_telegram_user_id on users (telegram_user_id);