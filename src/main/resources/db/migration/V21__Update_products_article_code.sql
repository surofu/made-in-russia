alter table products
    alter column article_code drop default,
    alter column article_code set default generate_unique_product_article_code(),
    alter column article_code set not null;

alter table products
    drop column if exists unique_products_article_code;

do
$$
    begin
        if not exists(select 1
                      from pg_constraint
                      where conname = 'unique_products_article_code'
                        and conrelid = 'products'::regclass) then
            execute 'alter table products add constraint unique_products_article_code unique (article_code);';
        end if;
    end;
$$;

do
$$
    begin
        alter table products
            add constraint check_products_article_code check ( length(article_code) = 9 );
    exception
        when duplicate_object then null;
    end;
$$;

create index if not exists idx_products_article_code ON products (article_code);