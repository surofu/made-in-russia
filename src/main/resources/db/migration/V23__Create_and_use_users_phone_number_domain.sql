do
$$
    begin
        if not exists(select 1
                      from pg_type t
                               join pg_namespace n on n.oid = t.typnamespace
                      where t.typname = 'phone_number_domain'
                        and n.nspname = current_schema()) then
            create domain phone_number_domain as varchar(255) not null
                check (
                    VALUE ~ '^\+?[0-9]{10,15}$'
                    );
        end if;
    end;
$$;

alter table users
    alter column phone_number type phone_number_domain;