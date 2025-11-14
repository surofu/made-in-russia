package com.surofu.exporteru.core.view;

import com.surofu.exporteru.application.converter.CategoryConverter;
import com.surofu.exporteru.application.converter.UserConverter;
import com.surofu.exporteru.application.dto.AbstractAccountDto;
import com.surofu.exporteru.application.dto.category.CategoryDto;
import com.surofu.exporteru.application.dto.DeliveryMethodDto;
import com.surofu.exporteru.application.converter.DeliveryMethodsConverter;
import com.surofu.exporteru.application.dto.vendor.VendorDto;
import com.surofu.exporteru.core.model.currency.CurrencyCode;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import jakarta.persistence.*;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Migrate ProductSummaryView Hstore -> Jsonb
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

    @Column(name = "title_translations", columnDefinition = "hstore")
    private String titleTranslations;

    @Column(name = "price_original_price")
    private BigDecimal originPrice;

    @Column(name = "price_discount")
    private BigDecimal discount;

    @Column(name = "price_discounted_price")
    private BigDecimal discountedPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_currency", nullable = false, columnDefinition = "currency")
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
        if (titleTranslations == null || lang == null) {
            return title;
        }

        Pattern pattern = Pattern.compile("\"" + lang + "\"=>\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(titleTranslations);

        return matcher.find() ? matcher.group(1) : title;
    }

    public String getAddressByLang(String lang) {
        if (user instanceof VendorDto vendor) {
            if (vendor.getVendorDetails() != null && vendor.getVendorDetails().getAddressTranslations() != null) {
                String addressTranslations = vendor.getVendorDetails().getAddressTranslations();
                return extractTranslationFromHstore(addressTranslations, lang);
            }
        }
        return null;
    }

    private String extractTranslationFromHstore(String hstore, String lang) {
        if (hstore == null || lang == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("\"" + lang + "\"=>\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(hstore);
        return matcher.find() ? matcher.group(1) : null;
    }
}
