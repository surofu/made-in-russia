alter table products
    add column if not exists minimum_order_quantity   int,
    add column if not exists discount_expiration_date timestamptz;

do
$$
    begin
        begin
            alter table products
                add constraint check_products_minimum_order_quantity check ( minimum_order_quantity >= 0 );

        exception
            when duplicate_object then
                raise notice 'constraint check_products_minimum_order_quantity already exists';
        end;
    end
$$;