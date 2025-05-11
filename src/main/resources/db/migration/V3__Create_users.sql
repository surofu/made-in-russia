do
$$
    begin
        create type user_role as enum ('ROLE_USER', 'ROLE_ADMIN');
    exception
        when duplicate_object then null;
    end
$$;

create table if not exists users
(
    id                     bigserial primary key,
    role                   user_role,
    email                  varchar(255) not null unique,
    login                  varchar(255) unique,
    registration_date      timestamptz  not null default now(),
    last_modification_date timestamptz  not null default now(),
    last_login_date        timestamptz  not null default now()
);

create table if not exists users_passwords
(
    id                     bigserial primary key,
    user_id                bigint       not null,
    password               varchar(255) not null,
    creation_date          timestamptz  not null default now(),
    last_modification_date timestamptz  not null default now(),

    constraint fk_users_passwords_user_id foreign key (user_id) references users (id)
);

-- create table if not exists users_favorites_products
-- (
--   user_id bigint not null,
--   product_id bigint not null,
--
--   constraint fk_users_favorites_products_user_id foreign key (user_id) references users (categoryId),
--   constraint fk_users_favorites_products_product_id foreign key (product_id) references products (categoryId)
-- );