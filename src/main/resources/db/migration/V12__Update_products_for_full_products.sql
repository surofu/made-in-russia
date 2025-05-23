do
$$
    begin
        if exists(select 1
                  from information_schema.columns
                  where table_name = 'products'
                    and column_name = 'image_url') then
            execute 'alter table products rename column image_url to preview_image_url;';
        end if;
    end
$$;

alter table products
    add column if not exists main_description    text check ( length(main_description) < 20000 ) not null default '',
    add column if not exists further_description text check ( length(further_description) < 5000 ) not null default '',
    add column if not exists summary_description text check ( length(summary_description) < 5000 ) not null default '',
    add column if not exists dtype               varchar(255)                                    not null default 'FullProduct';