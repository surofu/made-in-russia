do
$$
    begin
        if not exists(select 1
                      from pg_type t
                               join pg_namespace n on n.oid = t.typnamespace
                      where t.typname = 'email_domain'
                        and n.nspname = current_schema()) then
            create domain email_domain as varchar(255) not null
                check (
                    VALUE ~ '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$'
                    );
        end if;
    end;
$$;

alter table users
    alter column email type email_domain;