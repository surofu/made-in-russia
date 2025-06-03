do
$$
    begin
        begin
            create table if not exists product_characteristics
            (
                id                     bigserial,
                product_id             bigint       not null,
                name                   varchar(255) not null,
                value                  varchar(255) not null,
                creation_date          timestamptz  not null default now(),
                last_modification_date timestamptz  not null default now(),

                constraint pk_product_characteristics_id primary key (id),
                constraint fk_product_characteristics_product_id foreign key (product_id) references products (id)
            );

            create index if not exists idx_product_characteristics_product_id on product_characteristics (product_id);

            revoke update (creation_date) on product_characteristics from public;

            raise notice 'Product characteristics table and indexes have been successfully created';

        exception
            when others then
                raise notice 'Error when creating product characteristics table and indexes: %s', sqlerrm;
        end;
    end
$$;