ALTER TABLE similar_products
    DROP CONSTRAINT fk_similar_products_similar_product_id,
    ADD CONSTRAINT fk_similar_products_similar_product_id
        FOREIGN KEY (similar_product_id) REFERENCES products (id) ON DELETE CASCADE;