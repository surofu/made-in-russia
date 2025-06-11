do
$$
    begin
        begin
            alter table categories
                add column if not exists parent_category_id bigint,
                add constraint check_categories_parent_category_id check ( parent_category_id != id ),
                add constraint fk_categories_parent_category_id foreign key (parent_category_id) references categories (id);

        exception
            when duplicate_object then null;
        end;
    end
$$;