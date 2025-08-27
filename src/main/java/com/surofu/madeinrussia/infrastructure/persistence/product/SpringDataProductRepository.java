package com.surofu.madeinrussia.infrastructure.persistence.product;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.ProductArticleCode;
import com.surofu.madeinrussia.core.model.product.characteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.model.product.faq.ProductFaq;
import com.surofu.madeinrussia.core.model.product.media.ProductMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface SpringDataProductRepository extends JpaRepository<Product, Long> {

    @Query("select p.category from Product p where p.id = :productId")
    Optional<Category> getProductCategoryByProductId(@Param("productId") Long productId);

    Optional<Product> findByArticleCode(ProductArticleCode articleCode);

    @Query("select p.deliveryMethods from Product p where p.id = :productId")
    List<DeliveryMethod> getProductDeliveryMethodsByProductId(@Param("productId") Long productId);

    @Query("select m from ProductMedia m where m.product.id = :productId")
    Optional<List<ProductMedia>> getProductMediaByProductId(@Param("productId") Long productId);

    @Query("select c from ProductCharacteristic c where c.product.id = :productId")
    Optional<List<ProductCharacteristic>> getProductCharacteristicsByProductId(@Param("productId") Long productId);

    @Query("select faq from ProductFaq faq where faq.product.id = :productId")
    Optional<List<ProductFaq>> getProductFaqByProductId(@Param("productId") Long productId);

    @Query(value = """
            SELECT input.id
            FROM unnest(?1) WITH ORDINALITY AS input(id, ord)
            LEFT JOIN products p ON p.id = input.id
            WHERE p.id IS NULL
            ORDER BY input.ord
            LIMIT 1
            """, nativeQuery = true)
    Optional<Long> firstNotExists(Long[] productIdsArray);

    @Query(value = """
            SELECT
                                                 CASE
                                                     WHEN COUNT(r.rating) = 0 THEN NULL
                                                     ELSE CAST(ROUND(
                                                         CASE
                                                             WHEN AVG(r.rating) < 1.0 THEN 1.0
                                                             WHEN AVG(r.rating) > 5.0 THEN 5.0
                                                             ELSE AVG(r.rating)
                                                         END, 1) AS DOUBLE PRECISION)
                                                 END
                                             FROM product_reviews r
                                             WHERE r.product_id = :productId
            """, nativeQuery = true)
    Optional<Double> getProductRatingById(@Param("productId") Long productId);

    @Query(value = """
    select
    p.id as productId,
    coalesce(
        p.title_translations -> :lang,
        p.title
    )  as productTitle,
    p.preview_image_url as productImage,
    c.id as categoryId,
    coalesce(
        c.name_translations -> :lang,
        c.name
    ) as categoryName,
    c.image_url as categoryImage
    from products p
    join users u on u.id = p.user_id
    join categories c on c.id = p.category_id
    where (
            p.title ilike concat('%', :searchTerm, '%') or
            (p.title_translations::hstore -> 'en') ilike concat('%', :searchTerm, '%') or
            (p.title_translations::hstore -> 'ru') ilike concat('%', :searchTerm, '%') or
            (p.title_translations::hstore -> 'zh') ilike concat('%', :searchTerm, '%') or
            p.article_code ilike concat('%', :searchTerm, '%') or
            u.login ilike concat('%', :searchTerm, '%') or
            u.email ilike concat('%', :searchTerm, '%')
    ) and (:vendorId is null or u.id = :vendorId)
    order by c.name, p.title
    """, nativeQuery = true)
    List<SearchHintView> findHintViews(@Param("searchTerm") String searchTerm, @Param("vendorId") Long vendorId, @Param("lang") String lang);

    // View

    @Query(value = """
        select
        p.id,
        p.user_id as "userId",
        p.category_id,
        p.article_code as "articleCode",
        coalesce(
            p.title_translations -> :lang,
            p.title
        ) as title,
        coalesce(
            p.main_description_translations -> :lang,
            p.main_description
        ) as "mainDescription",
        coalesce(
            p.further_description_translations -> :lang,
            p.further_description
        ) as "furtherDescription",
        p.preview_image_url as "previewImageUrl",
        p.minimum_order_quantity as "minimumOrderQuantity",
        p.discount_expiration_date as "discountExpirationDate",
        p.creation_date as "creationDate",
        p.last_modification_date as "lastModificationDate",
        (
            select
                case
                    when count(pr.rating) = 0 then null
                    else cast(round(
                        case
                            when avg(pr.rating) < 1.0 then 1.0
                            when avg(pr.rating) > 5.0 then 5.0
                            else avg(pr.rating)
                        end, 1
                    ) as double precision)
                end
            from product_reviews pr
            where pr.product_id = :id
        ) as "rating",
        (
            select count(*)
            from product_reviews pr
            where pr.product_id = :id
        ) as "reviewsCount"
        from products p
        where p.id = :id
    """, nativeQuery = true)
    Optional<ProductView> findProductViewByIdAndLang(@Param("id") Long id, @Param("lang") String lang);

    @Query(value = """
        select
        p.id,
        p.user_id as "userId",
        p.category_id,
        p.article_code as "articleCode",
        coalesce(
            p.title_translations -> :lang,
            p.title
        ) as title,
        coalesce(
            p.main_description_translations -> :lang,
            p.main_description
        ) as "mainDescription",
        coalesce(
            p.further_description_translations -> :lang,
            p.further_description
        ) as "furtherDescription",
        p.preview_image_url as "previewImageUrl",
        p.minimum_order_quantity as "minimumOrderQuantity",
        p.discount_expiration_date as "discountExpirationDate",
        p.creation_date as "creationDate",
        p.last_modification_date as "lastModificationDate",
        (
            select
                case
                    when count(pr.rating) = 0 then null
                    else cast(round(
                        case
                            when avg(pr.rating) < 1.0 then 1.0
                            when avg(pr.rating) > 5.0 then 5.0
                            else avg(pr.rating)
                        end, 1
                    ) as double precision)
                end
            from product_reviews pr
            join products pp on pr.product_id = pp.id
            where pp.article_code = :article
        ) as "rating",
        (
            select count(*)
            from product_reviews pr
            join products pp on pr.product_id = pp.id
            where pp.article_code = :article
        ) as "reviewsCount"
        from products p
        where p.article_code = :article
    """, nativeQuery = true)
    Optional<ProductView> findProductViewByArticleCodeAndLang(@Param("article") String article, @Param("lang") String lang);

    @Query(value = """
    select
    p.id,
    coalesce(
        p.title_translations -> :lang,
        p.title
    ) as title,
    p.preview_image_url
    from products p
    join similar_products sp on p.id = sp.similar_product_id
    where sp.parent_product_id = :id
    """, nativeQuery = true)
    List<SimilarProductView> findAllSimilarProductViewByIdAndLang(@Param("id") Long id, @Param("lang") String lang);

    @Query(value = """
     select
        p.id,
        p.user_id as "userId",
        p.category_id,
        p.article_code as "articleCode",
        coalesce(
            p.title_translations -> :lang,
            p.title
        ) as title,
        p.title_translations::text as "titleTranslations",
        coalesce(
            p.main_description_translations -> :lang,
            p.main_description
        ) as "mainDescription",
        p.main_description_translations::text as "mainDescriptionTranslations",
        coalesce(
            p.further_description_translations -> :lang,
            p.further_description
        ) as "furtherDescription",
        p.further_description_translations::text as "furtherDescriptionTranslations",
        p.preview_image_url as "previewImageUrl",
        p.minimum_order_quantity as "minimumOrderQuantity",
        p.discount_expiration_date as "discountExpirationDate",
        p.creation_date as "creationDate",
        p.last_modification_date as "lastModificationDate",
        (
            select
                case
                    when count(pr.rating) = 0 then null
                    else cast(round(
                        case
                            when avg(pr.rating) < 1.0 then 1.0
                            when avg(pr.rating) > 5.0 then 5.0
                            else avg(pr.rating)
                        end, 1
                    ) as double precision)
                end
            from product_reviews pr
            where pr.product_id = :id
        ) as "rating",
        (
            select count(*)
            from product_reviews pr
            where pr.product_id = :id
        ) as "reviewsCount"
        from products p
        where p.id = :id
    """, nativeQuery = true)
    Optional<ProductWithTranslationsView> findProductWithTranslationsByIdAndLang(@Param("id") Long id, @Param("lang") String lang);
}
