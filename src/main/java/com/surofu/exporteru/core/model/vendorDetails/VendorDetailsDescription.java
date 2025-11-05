package com.surofu.exporteru.core.model.vendorDetails;

import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import com.surofu.exporteru.application.exception.LocalizedValidationException;
import com.surofu.exporteru.application.utils.HstoreParser;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorDetailsDescription implements Serializable {

    @Getter(AccessLevel.NONE)
    @Column(name = "description", nullable = false)
    private String value;

    @ColumnTransformer(write = "?::hstore")
    @Column(name = "description_translations", nullable = false, columnDefinition = "hstore")
    private String translations = HstoreParser.toString(HstoreTranslationDto.empty());

    private VendorDetailsDescription(String text) {
        if (text != null && text.length() > 20_000) {
            throw new LocalizedValidationException("validation.vendor.description.max_length");
        }

        this.value = Objects.requireNonNullElse(text, "");
    }

    public static VendorDetailsDescription of(String text) {
        VendorDetailsDescription description = new VendorDetailsDescription(text);
        description.setTranslations(HstoreTranslationDto.empty());
        return description;
    }

    public HstoreTranslationDto getTranslations() {
        return HstoreParser.fromString(Objects.requireNonNullElse(StringUtils.trimToNull(translations), ""));
    }

    public void setTranslations(HstoreTranslationDto dto) {
        this.translations = HstoreParser.toString(dto);
    }

    public String getValue() {
        Locale locale = LocaleContextHolder.getLocale();
        var valueNonNull = Objects.requireNonNullElse(value, "");

        return switch (locale.getLanguage()) {
            case "en" -> Objects.requireNonNullElse(StringUtils.trimToNull(getTranslations().textEn()), valueNonNull);
            case "ru" -> Objects.requireNonNullElse(StringUtils.trimToNull(getTranslations().textRu()), valueNonNull);
            case "zh" -> Objects.requireNonNullElse(StringUtils.trimToNull(getTranslations().textZh()), valueNonNull);
            default -> value;
        };
    }

    @Override
    public String toString() {
        return getValue();
    }
}
