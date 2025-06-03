do
$$
    begin
        begin
            create table if not exists product_prices
            (
                id                       bigserial,
                product_id               bigint         not null,
                quantity_from            int            not null,
                quantity_to              int            not null,
                quantity_unit            varchar(255)   not null,
                currency                 varchar(255)   not null,
                original_price           decimal(10, 2) not null,
                discount                 decimal(5, 2)  not null,
                minimum_order_quantity   int            not null,
                discount_expiration_date timestamptz    not null,
                creation_date            timestamptz    not null default now(),
                last_modification_date   timestamptz    not null default now(),

                constraint fk_product_prices_product_id foreign key (product_id) references products (id),
                constraint check_product_prices_quantity_from check ( quantity_from >= 0 ),
                constraint check_product_prices_quantity_to check ( quantity_to >= 0 ),
                constraint check_product_prices_original_price check ( original_price >= 0 ),
                constraint check_product_prices_discount check ( discount >= 0 and discount <= 100),
                constraint check_product_prices_minimum_order_quantity check ( minimum_order_quantity >= 0 )
            );

            create index if not exists idx_product_prices_product_id on product_prices (product_id);

            revoke update (creation_date) on product_prices from public;

            raise notice 'Product prices table and indexes have been successfully created';

        exception
            when others then
                raise notice 'Error when creating product prices and indexes table: %s', sqlerrm;
        end;
    end
$$;