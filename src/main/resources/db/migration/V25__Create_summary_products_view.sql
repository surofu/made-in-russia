CREATE OR REPLACE VIEW product_summary_view AS
SELECT p.id,
       p.title,
       p.price,
       p.discount,
       p.price_unit,
       p.preview_image_url,
       p.creation_date,
       p.last_modification_date,
       c.id                     AS category_id,
       c.name                   AS category_name,
       c.creation_date          AS category_creation_date,
       c.last_modification_date AS category_last_modification_date,
       jsonb_agg(
               jsonb_build_object(
                       'id', dm.id,
                       'name', dm.name,
                       'creationDate', dm.creation_date,
                       'lastModificationDate', dm.last_modification_date
               )
       )                        AS delivery_methods
FROM products p
         JOIN
     categories c ON p.category_id = c.id
         LEFT JOIN
     products_delivery_methods pdm ON p.id = pdm.product_id
         LEFT JOIN
     delivery_methods dm ON pdm.delivery_method_id = dm.id
GROUP BY p.id, c.id
ORDER BY p.id;