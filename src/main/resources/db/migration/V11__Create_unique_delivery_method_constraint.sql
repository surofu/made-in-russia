DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1
                       FROM information_schema.table_constraints
                       WHERE table_name = 'products_delivery_methods'
                         AND constraint_name = 'uk_product_delivery_method'
                         AND constraint_type = 'UNIQUE') THEN
            EXECUTE 'ALTER TABLE products_delivery_methods ADD CONSTRAINT uk_product_delivery_method UNIQUE (product_id, delivery_method_id)';
        END IF;
    END
$$;