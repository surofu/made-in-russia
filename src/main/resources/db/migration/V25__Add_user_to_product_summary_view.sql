drop view if exists product_summary_view;

create or replace view product_summary_view as
select p.id,
       p.article_code,
       p.title,
       p.preview_image_url,
       p.creation_date,
       p.last_modification_date,
       jsonb_build_object(
               'id', u.id,
               'role', u.role,
               'login', u.login,
               'email', u.email,
               'phoneNumber', u.phone_number,
               'region', u.region,
               'registrationDate', u.registration_date,
               'lastModificationDate', u.last_modification_date
       )                   as "user",
       jsonb_build_object(
               'id', c.id,
               'name', c.name,
               'creationDate', c.creation_date,
               'lastModificationDate', c.last_modification_date
       )                   as "category",
       pp.original_price   as price_original_price,
       pp.discount         as price_discount,
       pp.discounted_price as price_discounted_price,
       pp.currency         as price_currency,
       coalesce(
                       jsonb_agg(
                       jsonb_build_object(
                               'id', dm.id,
                               'name', dm.name,
                               'creationDate', dm.creation_date,
                               'lastModificationDate', dm.last_modification_date
                       )
                                ) filter (where dm.id is not null),
                       '[]'::jsonb
       )                   as delivery_methods,
       case
           when review_stats.review_count = 0 then null::numeric
           else round(review_stats.avg_rating, 1)
           end             as rating
from products p
         join users u on p.user_id = u.id
         join categories c on p.category_id = c.id
         left join products_delivery_methods pdm on p.id = pdm.product_id
         left join delivery_methods dm on pdm.delivery_method_id = dm.id
         left join lateral (select count(r.rating) as review_count, avg(r.rating) avg_rating
                            from product_reviews r
                            where r.product_id = p.id) review_stats on true
         left join lateral (select min_price.original_price,
                                   min_price.discount,
                                   (min_price.original_price * (1 - min_price.discount / 100)) as discounted_price,
                                   min_price.currency
                            from product_prices min_price
                            where p.id = min_price.product_id
                            order by (min_price.original_price * (1 - min_price.discount / 100))
                            limit 1
    ) pp on true
group by p.id,
         u.id,
         c.id,
         pp.original_price,
         pp.discount,
         pp.discounted_price,
         pp.currency,
         review_stats.review_count,
         review_stats.avg_rating,
         p.last_modification_date
order by p.last_modification_date desc;