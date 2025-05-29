package com.surofu.madeinrussia.core.view;

import com.surofu.madeinrussia.application.dto.DeliveryMethodDto;
import com.surofu.madeinrussia.application.converter.DeliveryMethodsConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Immutable
@NoArgsConstructor
@AllArgsConstructor
@Subselect("SELECT * FROM product_summary_view")
@Synchronize({"products", "categories", "delivery_methods", "products_delivery_methods"})
public final class ProductSummaryView implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "original_price")
    private BigDecimal originPrice;

    @Column(name = "discount")
    private BigDecimal discount;

    @Formula("original_price * (1 - discount / 100)")
    private BigDecimal discountedPrice;

    @Column(name = "price_unit")
    private String priceUnit;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "preview_image_url")
    private String previewImageUrl;

    @Column(name = "creation_date")
    private ZonedDateTime creationDate;

    @Column(name = "last_modification_date")
    private ZonedDateTime lastModificationDate;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "category_creation_date")
    private String categoryCreationDate;

    @Column(name = "category_last_modification_date")
    private String categoryLastModificationDate;

    @Convert(converter = DeliveryMethodsConverter.class)
    @Column(name = "delivery_methods", columnDefinition = "jsonb")
    private List<DeliveryMethodDto> deliveryMethods;
}
