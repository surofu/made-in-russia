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

alter table products
    drop constraint if exists check_products_article_code;

alter table products
    alter column article_code set data type article_code_domain;