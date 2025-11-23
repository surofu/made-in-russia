package com.surofu.exporteru.infrastructure.persistence.product;

import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristic;
import com.surofu.exporteru.core.model.product.faq.ProductFaq;
import com.surofu.exporteru.core.model.product.media.ProductMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SpringDataProductRepository extends JpaRepository<Product, Long> {

    @Query("select p.category from Product p where p.id = :productId")
    Optional<Category> getProductCategoryByProductId(@Param("productId") Long productId);

    @Query("select p.deliveryMethods from Product p where p.id = :productId")
    List<DeliveryMethod> getProductDeliveryMethodsByProductId(@Param("productId") Long productId);

    @Query("select m from ProductMedia m where m.product.id = :productId")
    Optional<List<ProductMedia>> getProductMediaByProductId(@Param("productId") Long productId);

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
                p.id as productId,
                COALESCE(
                    p.title_translations::jsonb ->> :lang,
                    p.title
                ) as productTitle,
                p.preview_image_url as productImage,
                c.id as categoryId,
                COALESCE(
                    c.name_translations::jsonb ->> :lang,
                    c.name
                ) as categoryName,
                c.image_url as categoryImage,
                c.slug as categorySlug
            FROM categories c
            LEFT JOIN products p ON c.id = p.category_id AND p.approve_status = 'APPROVED'
            WHERE
                -- Поиск по товарам (если они есть)
                (p.id IS NOT NULL AND (
                    p.title ILIKE CONCAT('%', :searchTerm, '%') OR
                    (p.title_translations::jsonb ->> 'en') ILIKE CONCAT('%', :searchTerm, '%') OR
                    (p.title_translations::jsonb ->> 'ru') ILIKE CONCAT('%', :searchTerm, '%') OR
                    (p.title_translations::jsonb ->> 'zh') ILIKE CONCAT('%', :searchTerm, '%') OR
                    (p.title_translations::jsonb ->> 'hi') ILIKE CONCAT('%', :searchTerm, '%') OR
                    p.article_code ILIKE CONCAT('%', :searchTerm, '%')
                ))
                OR
                -- Поиск по категориям (всегда)
                (
                    (c.name_translations::jsonb ->> 'en') ILIKE CONCAT('%', :searchTerm, '%') OR
                    (c.name_translations::jsonb ->> 'ru') ILIKE CONCAT('%', :searchTerm, '%') OR
                    (c.name_translations::jsonb ->> 'zh') ILIKE CONCAT('%', :searchTerm, '%') OR
                    (c.name_translations::jsonb ->> 'hi') ILIKE CONCAT('%', :searchTerm, '%')
                )
            ORDER BY
                COALESCE(c.name_translations::jsonb ->> :lang, c.name),
                COALESCE(p.title_translations::jsonb ->> :lang, p.title);
            """, nativeQuery = true)
    List<SearchHintView> findHintViews(@Param("searchTerm") String searchTerm, @Param("vendorId") Long vendorId, @Param("lang") String lang);

    // View

    @Query(value = """
                select
                p.id,
                p.approve_status as "approveStatus",
                p.user_id as "userId",
                p.category_id,
                p.article_code as "articleCode",
                coalesce(
                    p.title_translations::jsonb ->> :lang,
                    p.title
                ) as title,
                coalesce(
                    p.main_description_translations::jsonb ->> :lang,
                    p.main_description
                ) as "mainDescription",
                coalesce(
                    p.further_description_translations::jsonb ->> :lang,
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
                where p.id = :id and p.approve_status in (:approveStatuses)
            """, nativeQuery = true)
    Optional<ProductView> findProductViewByIdAndLangAndStatuses(@Param("id") Long id, @Param("lang") String lang, @Param("approveStatuses") List<String> approveStatuses);

    @Query(value = """
        select
        p.id,
        p.approve_status as "approveStatus",
        p.user_id as "userId",
        p.category_id,
        p.article_code as "articleCode",
        coalesce(
            p.title_translations::jsonb ->> :lang,
            p.title
        ) as title,
        coalesce(
            p.main_description_translations::jsonb ->> :lang,
            p.main_description
        ) as "mainDescription",
        coalesce(
            p.further_description_translations::jsonb ->> :lang,
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
        where p.article_code = :article and p.approve_status = 'APPROVED'
    """, nativeQuery = true)
    Optional<ProductView> findProductViewByArticleCodeAndLang(@Param("article") String article, @Param("lang") String lang);

    @Query(value = """
    select
    p.id,
    coalesce(
        p.title_translations::jsonb ->> :lang,
        p.title
    ) as title,
    p.preview_image_url
    from products p
    join similar_products sp on p.id = sp.similar_product_id
    where p.approve_status = 'APPROVED' and sp.parent_product_id = :id
    """, nativeQuery = true)
    List<SimilarProductView> findAllSimilarProductViewByIdAndLang(@Param("id") Long id, @Param("lang") String lang);

    @Query(value = """
     select
        p.id,
        p.user_id as "userId",
        p.category_id,
        p.article_code as "articleCode",
        coalesce(
            p.title_translations::jsonb ->> :lang,
            p.title
        ) as title,
        p.title_translations,
        coalesce(
            p.main_description_translations::jsonb ->> :lang,
            p.main_description
        ) as "mainDescription",
        p.main_description_translations,
        coalesce(
            p.further_description_translations::jsonb ->> :lang,
            p.further_description
        ) as "furtherDescription",
        p.further_description_translations,
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

    void deleteByUserId(Long userId);

    List<Product> findAllByIdInAndApproveStatus(Collection<Long> ids, ApproveStatus approveStatus);

    boolean existsByIdAndApproveStatus(Long id, ApproveStatus approveStatus);

    Optional<Product> findByIdAndApproveStatusIn(Long id, Collection<ApproveStatus> approveStatuses);

    @Query(value = """
            select
            p.id,
            coalesce(
                p.title_translations::jsonb ->> :lang,
                p.title
            )        as title,
            p.preview_image_url
            from products p
            order by p.id
            """, nativeQuery = true)
    List<ProductForReviewView> findProductForReviewViewsByLang(@Param("lang") String lang);

    @Query("select p from Product p join fetch p.user where p.id = :id")
    Optional<Product> findByIdWithUser(@Param("id") Long productId);

    @Query(value = "select count(f) > 0 from users_favorite_products f where f.user_id = :userId and f.product_id = :productId", nativeQuery = true)
    boolean existsByIdInUserFavorites(@Param("userId") Long userId, @Param("productId") Long productId);
}
