package com.surofu.exporteru.core.view;

import com.surofu.exporteru.application.converter.CategoryConverter;
import com.surofu.exporteru.application.converter.UserConverter;
import com.surofu.exporteru.application.dto.AbstractAccountDto;
import com.surofu.exporteru.application.dto.category.CategoryDto;
import com.surofu.exporteru.application.dto.DeliveryMethodDto;
import com.surofu.exporteru.application.converter.DeliveryMethodsConverter;
import com.surofu.exporteru.core.model.currency.CurrencyCode;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import jakarta.persistence.*;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import org.hibernate.type.SqlTypes;

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
    @Enumerated(EnumType.STRING)
    @Column(name = "approve_status")
    private ApproveStatus approveStatus;
    @Column(name = "title")
    private String title;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "title_translations")
    private Map<String, String> titleTranslations;
    @Column(name = "price_original_price")
    private BigDecimal originPrice;
    @Column(name = "price_discount")
    private BigDecimal discount;
    @Column(name = "price_discounted_price")
    private BigDecimal discountedPrice;
    @Enumerated(EnumType.STRING)
    @Column(name = "price_currency")
    private CurrencyCode priceCurrencyCode;
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
    private AbstractAccountDto user;
    @Convert(converter = CategoryConverter.class)
    @Column(name = "category", columnDefinition = "jsonb")
    private CategoryDto category;
    @Convert(converter = DeliveryMethodsConverter.class)
    @Column(name = "delivery_methods", columnDefinition = "jsonb")
    private List<DeliveryMethodDto> deliveryMethods;

    public String getTitleByLang(String lang) {
        return titleTranslations.getOrDefault(lang, Objects.requireNonNullElse(title, ""));
    }
}
