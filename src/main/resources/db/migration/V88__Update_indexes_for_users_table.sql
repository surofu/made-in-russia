drop index if exists idx_users_login;
drop index if exists idx_users_email;
drop index if exists idx_users_phone_number;
drop index if exists idx_user_passwords_user_id;

create unique index idx_users_login on users (login);
create unique index idx_users_email on users (email);
create unique index idx_users_phone_number on users (phone_number);
create unique index idx_user_passwords_user_id on user_passwords (user_id);
