do
$$
    begin
        begin
            alter table categories
                add column if not exists slug varchar(255);

            update categories c
            set slug = concat(c.id, '_slug')
            where slug is null;

            alter table categories
                alter column slug set not null;

        exception
            when duplicate_object then null;
        end;
    end
$$;

do
$$
    begin
        begin
            alter table categories
                add constraint uk_categories_slug unique (slug);
        exception
            when others then null;
        end;
    end
$$;