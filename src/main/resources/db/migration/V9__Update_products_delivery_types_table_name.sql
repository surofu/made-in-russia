DO
$$
    BEGIN
        IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'products_delivery_types')
            AND NOT EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'products_delivery_methods') THEN
            EXECUTE 'ALTER TABLE products_delivery_types RENAME TO products_delivery_methods';
        ELSE
            RAISE NOTICE 'Таблица products_delivery_types не существует или products_delivery_methods уже существует';
        END IF;
    END
$$;