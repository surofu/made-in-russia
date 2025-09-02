package com.surofu.madeinrussia.application.utils;

import com.surofu.madeinrussia.application.dto.product.ProductDto;
import com.surofu.madeinrussia.application.dto.product.ProductPackageOptionDto;
import com.surofu.madeinrussia.application.dto.product.ProductPriceDto;
import com.surofu.madeinrussia.core.model.currency.CurrencyCode;
import com.surofu.madeinrussia.core.service.currency.CurrencyConverterService;
import com.surofu.madeinrussia.core.view.ProductSummaryView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalizationManager {

    private final MessageSource messageSource;
    private final CurrencyConverterService currencyConverterService;

    public String localize(String messageCode, Locale locale, Object... args) {
        String messageTemplate = messageSource.getMessage(messageCode, null, locale);
        return String.format(messageTemplate, args);
    }

    public String localize(String messageCode, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return localize(messageCode, locale, args);
    }

    public ProductSummaryView localizePrice(ProductSummaryView view, Locale locale) {
        CurrencyCode from = view.getPriceCurrencyCode();
        CurrencyCode to = switch (locale.getLanguage()) {
            case ("en") -> CurrencyCode.USD;
            case ("ru") -> CurrencyCode.RUB;
            case ("zh") -> CurrencyCode.CNY;
            default -> from;
        };

        try {
            BigDecimal localizedOriginalPrice = currencyConverterService.convert(from, to, view.getOriginPrice());
            BigDecimal localizedDiscountedPrice = currencyConverterService.convert(from, to, view.getDiscountedPrice());

            view.setOriginPrice(localizedOriginalPrice);
            view.setDiscountedPrice(localizedDiscountedPrice);
            view.setPriceCurrencyCode(to);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return view;
    }

    public ProductDto localizePrice(ProductDto dto, Locale locale) {
        CurrencyCode from = CurrencyCode.USD;
        CurrencyCode to = switch (locale.getLanguage()) {
            case ("en") -> CurrencyCode.USD;
            case ("ru") -> CurrencyCode.RUB;
            case ("zh") -> CurrencyCode.CNY;
            default -> from;
        };

        if (!dto.getPrices().isEmpty()) {
            from = CurrencyCode.valueOf(dto.getPrices().get(0).getCurrency());
        }

        for (ProductPackageOptionDto packagingOption : dto.getPackagingOptions()) {
            try {
                BigDecimal convertedPrice = currencyConverterService.convert(from, to, packagingOption.getPrice());
                packagingOption.setPrice(convertedPrice);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        for (ProductPriceDto price : dto.getPrices()) {
            try {
                BigDecimal localizedOriginalPrice = currencyConverterService.convert(from, to, price.getOriginalPrice());
                BigDecimal localizedDiscountedPrice = currencyConverterService.convert(from, to, price.getDiscountedPrice());

                price.setOriginalPrice(localizedOriginalPrice);
                price.setDiscountedPrice(localizedDiscountedPrice);
                price.setCurrency(to.name());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return dto;
    }
}
