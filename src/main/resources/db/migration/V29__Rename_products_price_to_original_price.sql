do
$$
    begin
        if exists(select 1
                  from information_schema.columns
                  where table_name = 'products'
                    and column_name = 'price') then
            execute 'alter table products rename column price to original_price;';
        end if;
    end
$$;