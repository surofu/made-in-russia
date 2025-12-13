drop view if exists product_summary_view;

create or replace view product_summary_view as
select p.id,
       p.approve_status,
       p.article_code,
       p.title,
       p.title_translations,
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
               'lastModificationDate', u.last_modification_date,
               'vendorDetails', CASE
                                    WHEN vd.id IS NOT NULL THEN
                                        jsonb_build_object(
                                                'id', vd.id,
                                                'inn', vd.inn,
                                                'address', vd.address,
                                                'addressTranslations', vd.address_translations,
                                                'description', vd.description,
                                                'phoneNumbers', COALESCE(
                                                        (SELECT jsonb_agg(DISTINCT vpn.phone_number)
                                                         FROM vendor_details_phone_numbers vpn
                                                         WHERE vpn.vendor_details_id = vd.id),
                                                        '[]'::jsonb
                                                                ),
                                                'emails', COALESCE(
                                                        (SELECT jsonb_agg(DISTINCT ve.email)
                                                         FROM vendor_details_emails ve
                                                         WHERE ve.vendor_details_id = vd.id),
                                                        '[]'::jsonb
                                                          ),
                                                'sites', COALESCE(
                                                        (SELECT jsonb_agg(DISTINCT vs.url)
                                                         FROM vendor_details_sites vs
                                                         WHERE vs.vendor_details_id = vd.id),
                                                        '[]'::jsonb
                                                         ),
                                                'media', COALESCE(
                                                        (SELECT jsonb_agg(
                                                                        jsonb_build_object(
                                                                                'id', vm.id,
                                                                                'url', vm.url,
                                                                                'media_type', vm.media_type,
                                                                                'mime_type', vm.mime_type,
                                                                                'order', vm.position,
                                                                                'creationDate', vm.creation_date,
                                                                                'lastModificationDate',
                                                                                vm.last_modification_date
                                                                        )
                                                                )
                                                         FROM vendor_details_media vm
                                                         WHERE vm.vendor_details_id = vd.id),
                                                        '[]'::jsonb
                                                         ),
                                                'countries', COALESCE(
                                                        (SELECT jsonb_agg(
                                                                        jsonb_build_object(
                                                                                'id', vc.id,
                                                                                'name', vc.name,
                                                                                'name_translations',
                                                                                vc.name_translations,
                                                                                'creationDate', vc.creation_date,
                                                                                'lastModificationDate',
                                                                                vc.last_modification_date
                                                                        )
                                                                )
                                                         FROM vendor_countries vc
                                                         WHERE vc.vendor_details_id = vd.id),
                                                        '[]'::jsonb
                                                             ),
                                                'productCategories', COALESCE(
                                                        (SELECT jsonb_agg(
                                                                        jsonb_build_object(
                                                                                'id', vpc.id,
                                                                                'name', vpc.name,
                                                                                'creationDate', vpc.creation_date,
                                                                                'lastModificationDate',
                                                                                vpc.last_modification_date
                                                                        )
                                                                )
                                                         FROM vendor_product_categories vpc
                                                         WHERE vpc.vendor_details_id = vd.id),
                                                        '[]'::jsonb
                                                                     ),
                                                'faq', COALESCE(
                                                        (SELECT jsonb_agg(
                                                                        jsonb_build_object(
                                                                                'id', vf.id,
                                                                                'question', vf.question,
                                                                                'answer', vf.answer,
                                                                                'creationDate', vf.creation_date,
                                                                                'lastModificationDate',
                                                                                vf.last_modification_date
                                                                        )
                                                                )
                                                         FROM vendor_faq vf
                                                         WHERE vf.vendor_details_id = vd.id),
                                                        '[]'::jsonb
                                                       ),
                                                'viewsCount', COALESCE(
                                                        (SELECT count(*)
                                                         FROM vendor_views vv
                                                         WHERE vv.vendor_details_id = vd.id),
                                                        0
                                                              ),
                                                'creationDate', vd.creation_date,
                                                'lastModificationDate', vd.last_modification_date
                                        )
                                    ELSE NULL
                   END
       )                 as "user",
       jsonb_build_object(
               'id', c.id,
               'name', c.name,
               'slug', c.slug,
               'imageUrl', c.image_url,
               'childrenCount', (SELECT COUNT(*) FROM categories cc WHERE cc.parent_category_id = c.id),
               'creationDate', c.creation_date,
               'lastModificationDate', c.last_modification_date
       )                 AS "category",

       pp.original_price as price_original_price,
       case
           when p.discount_expiration_date >= now()
               then pp.discount
           else 0::decimal(5, 2)
           end           as price_discount,
       case
           when p.discount_expiration_date >= now()
               then case
                        when pp.discounted_price >= 0 and pp.discounted_price < 1
                            then 1::decimal(5, 2)
                        else pp.discounted_price
               end
           else pp.original_price
           end           as price_discounted_price,
       pp.currency       as price_currency,
       coalesce(
               (SELECT jsonb_agg(
                               jsonb_build_object(
                                       'id', dm.id,
                                       'name', dm.name,
                                       'creationDate', dm.creation_date,
                                       'lastModificationDate', dm.last_modification_date
                               )
                       )
                FROM products_delivery_methods pdm
                         JOIN delivery_methods dm ON pdm.delivery_method_id = dm.id
                WHERE pdm.product_id = p.id),
               '[]'::jsonb
       )                 as delivery_methods,
       case
           when review_stats.review_count = 0 then null::numeric
           else round(review_stats.avg_rating, 1)
           end           as rating
from products p
         join users u on p.user_id = u.id
         left join vendor_details vd on u.id = vd.user_id
         join categories c on p.category_id = c.id
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
         p.last_modification_date,
         vd.id
order by p.last_modification_date desc;