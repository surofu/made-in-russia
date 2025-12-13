do
$$
    begin
        begin
            alter table products
                add column if not exists user_id bigint default null;

            update products
            set user_id = (select id from users where role = 'ROLE_VENDOR' limit 1)
            where user_id is null;

            alter table products
                alter column user_id drop default,
                alter column user_id set not null,
                add constraint fk_products_user_id foreign key (user_id) references users (id);

            create index if not exists idx_products_user_id on products (user_id);

            raise notice 'Successfully added author (user_id) to the products table';

        exception
            when duplicate_object then
                raise notice 'Duplicate object when adding product author: %s', sqlerrm;
            when others then
                raise notice 'Error when adding product author: %s', sqlerrm;
        end;
    end
$$;