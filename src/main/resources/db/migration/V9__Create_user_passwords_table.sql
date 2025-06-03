do
$$
    begin
        begin
            create table if not exists user_passwords
            (
                id                     bigserial,
                user_id                bigint      not null,
                password               text        not null check ( length(password) >= 4 and length(password) <= 10000 ),
                creation_date          timestamptz not null default now(),
                last_modification_date timestamptz not null default now(),

                constraint pk_user_passwords_id primary key (id),
                constraint fk_user_passwords_user_id foreign key (user_id) references users (id)
            );

            create index if not exists idx_user_passwords_user_id on user_passwords (user_id);

            revoke update (creation_date) on user_passwords from public;

            raise notice 'User password table have been successfully created';

        exception
            when others then
                raise notice 'Error when creating user passwords table: %s', sqlerrm;
        end;
    end
$$;