package com.surofu.exporteru.core.model.vendorDetails.faq;

import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import com.surofu.exporteru.application.exception.LocalizedValidationException;
import com.surofu.exporteru.application.utils.HstoreParser;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorFaqAnswer implements Serializable {

    @Column(name = "answer", nullable = false)
    private String value;

    // TODO: VendorFaqAnswer Translation. Hstore -> Jsonb
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ColumnTransformer(write = "?::hstore")
    @Column(name = "answer_translations", nullable = false, columnDefinition = "hstore")
    private String translations = HstoreParser.toString(HstoreTranslationDto.empty());

    private VendorFaqAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.faq.answer.empty");
        }

        if (answer.length() >= 20_000) {
            throw new LocalizedValidationException("validation.faq.answer.max_length");
        }

        this.value = answer;
    }

    public static VendorFaqAnswer of(String answer) {
        return new VendorFaqAnswer(answer);
    }

    public HstoreTranslationDto getTranslations() {
        return HstoreParser.fromString(translations);
    }

    public void setTranslations(HstoreTranslationDto translations) {
        this.translations = HstoreParser.toString(translations);
    }

    @Override
    public String toString() {
        return value;
    }
}
