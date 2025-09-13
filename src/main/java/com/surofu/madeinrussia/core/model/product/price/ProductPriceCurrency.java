package com.surofu.madeinrussia.core.model.product.price;

import com.surofu.madeinrussia.application.exception.LocalizedValidationException;
import com.surofu.madeinrussia.core.model.currency.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPriceCurrency implements Serializable {

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, columnDefinition = "currency")
    private CurrencyCode value;

    private ProductPriceCurrency(String currencyString) {
        if (currencyString == null || currencyString.trim().isEmpty()) {
            throw new LocalizedValidationException("validation.product.price.currency.empty");
        }

        CurrencyCode currencyCode;

        try {
            currencyCode = CurrencyCode.valueOf(currencyString);
        } catch (IllegalArgumentException e) {
            throw new LocalizedValidationException("validation.product.price.currency.type", currencyString);
        }

        this.value = currencyCode;
    }

    public static ProductPriceCurrency of(String currency) {
        return new ProductPriceCurrency(currency);
    }

    @Override
    public String toString() {
        return value.name();
    }
}
