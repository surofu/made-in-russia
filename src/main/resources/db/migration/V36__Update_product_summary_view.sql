drop view if exists product_summary_view;

CREATE OR REPLACE VIEW product_summary_view AS
SELECT p.id,
       p.title,
       CASE
           WHEN review_stats.review_count = 0 THEN NULL
           ELSE ROUND(GREATEST(1.0, LEAST(5.0, review_stats.avg_rating)), 1)
           END                                     AS rating,
       p.preview_image_url,
       p.creation_date,
       p.last_modification_date,
       c.id                                        AS category_id,
       c.name                                      AS category_name,
       c.creation_date                             AS category_creation_date,
       c.last_modification_date                    AS category_last_modification_date,
       pp.original_price                           AS original_price,
       pp.discount                                 AS discount,
       pp.unit                                     AS price_unit,
       jsonb_agg(
       jsonb_build_object(
               'id', dm.id,
               'name', dm.name,
               'creationDate', dm.creation_date,
               'lastModificationDate', dm.last_modification_date
       )
                ) FILTER (WHERE dm.id IS NOT NULL) AS delivery_methods
FROM products p
         JOIN
     categories c ON p.category_id = c.id
         LEFT JOIN
     products_delivery_methods pdm ON p.id = pdm.product_id
         LEFT JOIN LATERAL (
    select *
    from product_prices ppmin
    where p.id = ppmin.product_id
    order by ppmin.original_price
    limit 1
    ) pp on true
         LEFT JOIN
     delivery_methods dm ON pdm.delivery_method_id = dm.id
         LEFT JOIN LATERAL (
    SELECT COUNT(r.rating) AS review_count,
           AVG(r.rating)   AS avg_rating
    FROM product_reviews r
    WHERE r.product_id = p.id
    ) review_stats ON true
GROUP BY p.id, c.id, pp.original_price, pp.discount, pp.unit, review_stats.review_count, review_stats.avg_rating
ORDER BY p.id;