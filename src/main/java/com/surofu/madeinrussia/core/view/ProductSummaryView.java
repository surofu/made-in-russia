package com.surofu.madeinrussia.core.view;

import com.surofu.madeinrussia.application.converter.CategoryConverter;
import com.surofu.madeinrussia.application.converter.UserConverter;
import com.surofu.madeinrussia.application.dto.CategoryDto;
import com.surofu.madeinrussia.application.dto.DeliveryMethodDto;
import com.surofu.madeinrussia.application.converter.DeliveryMethodsConverter;
import com.surofu.madeinrussia.application.dto.UserDto;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Synchronize({"products", "categories", "delivery_methods", "products_delivery_methods", "users"})
public final class ProductSummaryView implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "price_original_price")
    private BigDecimal originPrice;

    @Column(name = "price_discount")
    private BigDecimal discount;

    @Column(name = "price_discounted_price")
    private BigDecimal discountedPrice;

    @Column(name = "price_currency")
    private String priceCurrency;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "preview_image_url")
    private String previewImageUrl;

    @Column(name = "creation_date")
    private ZonedDateTime creationDate;

    @Column(name = "last_modification_date")
    private ZonedDateTime lastModificationDate;

    @Convert(converter = UserConverter.class)
    @Column(name = "user", columnDefinition = "jsonb")
    private UserDto user;

    @Convert(converter = CategoryConverter.class)
    @Column(name = "category", columnDefinition = "jsonb")
    private CategoryDto category;

    @Convert(converter = DeliveryMethodsConverter.class)
    @Column(name = "delivery_methods", columnDefinition = "jsonb")
    private List<DeliveryMethodDto> deliveryMethods;
}
