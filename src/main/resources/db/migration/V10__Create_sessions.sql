do
$$
    begin
        begin
            create table if not exists sessions
            (
                id                     bigserial,
                user_id                bigint       not null,
                device_id              text         not null,
                device_type            varchar(255) not null,
                browser                varchar(255) not null,
                os                     varchar(255) not null,
                ip_address             cidr         not null,
                creation_date          timestamptz  not null default now(),
                last_modification_date timestamptz  not null default now(),

                constraint pk_sessions_id primary key (id),
                constraint uk_sessions_user_id_device_id unique (user_id, device_id),
                constraint uk_sessions_device_id_user_id unique (device_id, user_id),
                constraint fk_sessions_user_id foreign key (user_id) references users (id)
            );

            create index if not exists idx_sessions_user_id_device_id on sessions (user_id, device_id);
            create index if not exists idx_sessions_device_id_user_id on sessions (device_id, user_id);

            revoke update (device_id) on sessions from public;
            revoke update (creation_date) on sessions from public;

            raise notice 'Sessions table and indexes have been successfully created';

        exception
            when others then
                raise notice 'Error when creating sessions table and indexes: %s', sqlerrm;
        end;
    end
$$;