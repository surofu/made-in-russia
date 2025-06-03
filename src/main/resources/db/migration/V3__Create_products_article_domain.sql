do
$$
    begin
        if not exists(select 1
                      from pg_type t
                               join pg_namespace n on n.oid = t.typnamespace
                      where t.typname = 'article_code_domain'
                        and n.nspname = current_schema()) then
            create domain article_code_domain as varchar(255) not null
                check (
                    VALUE ~ '^[A-Za-z]{4}-[0-9]{4}$'
                    );
        end if;
    end;
$$;