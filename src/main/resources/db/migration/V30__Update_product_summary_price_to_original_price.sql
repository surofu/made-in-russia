DO
$$
    BEGIN
        IF EXISTS (SELECT 1
                   FROM information_schema.columns
                   WHERE table_name = 'product_summary_view'
                     AND column_name = 'price') THEN
            EXECUTE 'ALTER VIEW product_summary_view RENAME COLUMN price TO original_price;';
        END IF;
    END
$$;