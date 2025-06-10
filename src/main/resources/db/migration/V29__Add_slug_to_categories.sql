do
$$
    begin
        alter table categories
            add column if not exists slug varchar(255);

        update categories c
        set slug = concat(c.id, '_slug')
        where slug is null;

        alter table categories
        alter column slug set not null;

        begin
            alter table categories
            add constraint uk_categories_slug unique (slug);

            exception
        when duplicate_object then
            raise notice 'constraint uk_categories_slug already exists';
        end;
    end;
$$;