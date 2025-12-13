do
$$
    begin
        begin
            create table if not exists users
            (
                id                     bigserial,
                role                   user_role           not null default 'ROLE_USER',
                login                  varchar(255)        not null,
                email                  email_domain        not null,
                phone_number           phone_number_domain not null,
                region                 varchar(255)        not null,
                registration_date      timestamptz         not null default now(),
                last_modification_date timestamptz         not null default now(),

                constraint pk_users_id primary key (id),
                constraint uk_users_login unique (login),
                constraint uk_users_email unique (email),
                constraint uk_users_phone_number unique (phone_number)
            );

            create index if not exists idx_users_login on users (login);
            create index if not exists idx_users_email on users (email);
            create index if not exists idx_users_phone_number on users (phone_number);

            revoke update (registration_date) on users from public;

            raise notice 'Users table and indexes have been successfully created';

        exception
            when others then
                raise notice 'Error when creating users table and indexes: %s', sqlerrm;
        end;
    end
$$;