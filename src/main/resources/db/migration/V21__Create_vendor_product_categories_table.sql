do
$$
    begin
        begin
            create table if not exists vendor_product_categories
            (
                id                     bigserial,
                user_id                bigint       not null,
                name                   varchar(255) not null,
                creation_date          timestamptz  not null default now(),
                last_modification_date timestamptz  not null default now(),

                constraint pk_vendor_product_categories_id primary key (id),
                constraint fk_vendor_product_categories_user_id foreign key (user_id) references users (id)
            );

            create index if not exists idx_vendor_product_categories_user_id on vendor_product_categories (user_id);

            revoke update (creation_date) on vendor_product_categories from public;

            raise notice 'Table vendor product categories successfully created';
        exception
            when others then
                raise notice 'Error when creating vendor product categories table: %s', sqlerrm;
        end;
    end
$$;